# TrustNet Android

## Chạy API kết nối Neon

Cần Node.js 22 và PostgreSQL trên Neon. Trong thư mục `server`, sao chép `.env.example` thành `.env` và đặt `NEON_CONNECTION_STRING`. Biến này chỉ dùng trên server.

```powershell
cd server
npm ci
node --env-file=.env index.mjs --migrate
npm start
```

Mặc định API lắng nghe trên `127.0.0.1:8787`. Kiểm tra bằng `GET /health`. Migration thêm mật khẩu, phiên và chủ sở hữu lịch sử. Dữ liệu lịch sử cũ chưa có chủ sở hữu được giữ lại và không hiển thị trong tài khoản. Tài khoản cũ chưa có mật khẩu cần luồng đặt lại mật khẩu trước khi đăng nhập.

## Chạy Android

Mở thư mục gốc bằng Android Studio với Gradle 9.3.1 và SDK 36.1. Trình giả lập dùng `http://10.0.2.2:8787`. Với thiết bị qua USB, chạy `adb reverse tcp:8787 tcp:8787`, đặt `TRUSTNET_API_URL=http://127.0.0.1:8787` trong `.env` ở thư mục gốc rồi build lại. Bản phát hành cần URL HTTPS của API và thông tin ký release. Dùng `gradle testDebugUnitTest assembleDebug` để test và build.

Nhập khóa Gemini trong Cá nhân để bật phân tích trong phiên hiện tại. Khi thử nghiệm cục bộ có thể đặt `GEMINI_API_KEY` trong `.env` Android. Khóa cấu hình lúc build có thể đọc được từ APK, vì vậy hãy để trống khi phân phối. Ứng dụng ưu tiên Gemini 2.5 Flash-Lite, dùng 3.1 Flash-Lite khi khóa không có quyền dùng 2.5.

TrustCheck phân tích dấu hiệu từ link, tin nhắn, số điện thoại và ảnh. TrustAI sàng lọc ảnh và video tối đa 10 MB. Hai chức năng này không xác thực chủ sở hữu tên miền hay nguồn gốc nội dung.
