import { createServer } from 'node:http';
import { createHash, randomBytes, randomUUID, scrypt, timingSafeEqual } from 'node:crypto';
import { readFile } from 'node:fs/promises';
import { promisify } from 'node:util';
import pg from 'pg';

const connection = new URL(process.env.NEON_CONNECTION_STRING);
connection.searchParams.set('sslmode', 'verify-full');
const db = new pg.Pool({ connectionString: connection.href, enableChannelBinding: true, connectionTimeoutMillis: 10000 });
const passwordKey = promisify(scrypt);
const hashToken = (token) => createHash('sha256').update(token).digest('hex');
const profile = 'id, email, display_name AS name, COALESCE(safety_score, 0) AS level_score';

if (process.argv.includes('--migrate')) {
  await db.query(await readFile(new URL('./schema.sql', import.meta.url), 'utf8'));
  await db.end();
  console.log('Neon schema ready');
} else {
  await db.query('SELECT 1');
  createServer(handle).listen(Number(process.env.PORT || 8787), process.env.HOST || '127.0.0.1', () => {
    console.log('TrustNet API ready');
  });
}

async function handle(req, res) {
  const send = (status, value) => {
    res.writeHead(status, { 'Content-Type': 'application/json', 'Cache-Control': 'no-store' });
    res.end(JSON.stringify(value));
  };

  try {
    const route = req.method + ' ' + req.url;
    if (route === 'GET /health') {
      await db.query('SELECT 1');
      return send(200, { ok: true });
    }
    if (route === 'POST /login' || route === 'POST /register') {
      const body = await readBody(req);
      const email = String(body.email || '').trim().toLowerCase();
      const password = body.password;
      if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email) || email.length > 254 ||
          typeof password !== 'string' || password.length < 8 || password.length > 256) {
        return send(400, { error: 'invalid_credentials' });
      }

      let user;
      if (route === 'POST /register') {
        const name = String(body.name || '').trim();
        if (!name || name.length > 120) return send(400, { error: 'invalid_name' });
        const salt = randomBytes(16).toString('hex');
        const key = await passwordKey(password, salt, 64);
        const result = await db.query(
          'INSERT INTO users (id,email,display_name,password_hash,safety_score) VALUES ($1,$2,$3,$4,0) ON CONFLICT (email) DO NOTHING RETURNING ' + profile,
          [randomUUID(), email, name, salt + ':' + key.toString('hex')]);
        user = result.rows[0];
        if (!user) return send(409, { error: 'email_in_use' });
      } else {
        const result = await db.query('SELECT ' + profile + ', password_hash FROM users WHERE email=$1', [email]);
        const candidate = result.rows[0];
        const [salt, encoded] = (candidate?.password_hash || '').split(':');
        const actual = await passwordKey(password, salt || 'trustnet-no-password', 64);
        if (!encoded || !/^[0-9a-f]{128}$/.test(encoded) || !timingSafeEqual(actual, Buffer.from(encoded, 'hex'))) {
          return send(401, { error: 'invalid_credentials' });
        }
        const { password_hash, ...safeProfile } = candidate;
        user = safeProfile;
      }
      const token = randomBytes(32).toString('hex');
      await db.query("INSERT INTO trustnet_sessions VALUES ($1,$2,now()+interval '30 days')", [hashToken(token), user.id]);
      return send(200, { user, token });
    }
    if (route === 'GET /alerts') {
      const result = await db.query('SELECT id,title,summary,tag,time_ago,is_urgent FROM security_alerts ORDER BY created_at DESC LIMIT 20');
      return send(200, { rows: result.rows });
    }

    const token = req.headers.authorization?.match(/^Bearer ([a-f0-9]{64})$/)?.[1];
    if (!token) return send(401, { error: 'login_required' });
    const tokenHash = hashToken(token);
    const session = await db.query('SELECT user_id FROM trustnet_sessions WHERE token_hash=$1 AND expires_at>now()', [tokenHash]);
    const userId = session.rows[0]?.user_id;
    if (!userId) return send(401, { error: 'login_required' });

    switch (route) {
      case 'POST /logout':
        await db.query('DELETE FROM trustnet_sessions WHERE token_hash=$1', [tokenHash]);
        return send(200, { ok: true });
      case 'GET /history': {
        const result = await db.query('SELECT id,target,type,risk_score,risk_level,red_flags,recommendations,extract(epoch from created_at)*1000 AS ts FROM scan_history WHERE user_id=$1 ORDER BY created_at DESC LIMIT 50', [userId]);
        return send(200, { rows: result.rows });
      }
      case 'DELETE /history':
        await db.query('DELETE FROM scan_history WHERE user_id=$1', [userId]);
        return send(200, { ok: true });
      case 'POST /history': {
        const body = await readBody(req);
        if (!Number.isInteger(body.risk_score) || body.risk_score < 0 || body.risk_score > 100 ||
            !['URL','SMS','PHONE','IMAGE'].includes(body.type) ||
            !['SAFE','MODERATE','HIGH_RISK','UNKNOWN'].includes(body.risk_level)) {
          return send(400, { error: 'invalid_scan' });
        }
        await db.query('INSERT INTO scan_history (id,target,type,risk_score,risk_level,red_flags,recommendations,user_id) VALUES ($1,$2,$3,$4,$5,$6,$7,$8) ON CONFLICT (id) DO NOTHING',
          [field(body,'id',64),field(body,'target',16000),body.type,body.risk_score,body.risk_level,field(body,'red_flags',16000,true),field(body,'recommendations',16000,true),userId]);
        return send(200, { ok: true });
      }
      case 'POST /reports': {
        const body = await readBody(req);
        await db.query("INSERT INTO incident_reports (id,target,details,status) VALUES ($1,$2,$3,'PENDING')",
          [randomUUID(), field(body,'target',2000), field(body,'details',16000)]);
        return send(200, { ok: true });
      }
      case 'POST /volunteers': {
        const body = await readBody(req);
        await db.query('INSERT INTO volunteer_registrations (id,full_name,school,phone) VALUES ($1,$2,$3,$4)',
          [randomUUID(), field(body,'name',200), field(body,'school',300), field(body,'phone',40,true) || null]);
        return send(200, { ok: true });
      }
      default:
        return send(404, { error: 'route_not_found' });
    }
  } catch (error) {
    if (error instanceof SyntaxError || error instanceof RangeError) return send(400, { error: 'invalid_body' });
    console.error('TrustNet API error', error.code || error.name);
    return send(503, { error: 'service_unavailable' });
  }
}

async function readBody(req) {
  const chunks = [];
  let size = 0;
  for await (const chunk of req) {
    size += chunk.length;
    if (size > 65536) throw new RangeError('body too large');
    chunks.push(chunk);
  }
  return JSON.parse(Buffer.concat(chunks).toString('utf8'));
}

function field(body, name, limit, optional = false) {
  const value = body[name];
  if (typeof value !== 'string' || value.length > limit || (!optional && !value.trim())) {
    throw new RangeError(name);
  }
  return value;
}
