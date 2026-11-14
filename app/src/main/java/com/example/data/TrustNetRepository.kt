package com.example.data

import android.util.Log
import com.example.R
import com.example.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TrustNetRepository(
    val neonClient: NeonDatabaseClient = NeonDatabaseClient(),
    val aiService: GeminiAiService = GeminiAiService()
) {
    companion object {
        private const val TAG = "TrustNetRepository"
    }

    private val scope = CoroutineScope(Dispatchers.Default)

    // User Profile & Authentication State
    private val _currentUser = MutableStateFlow(
        UserProfile(
            id = "usr-01",
            name = "Amelia Andra",
            email = "amelia.andra@trustnet.org",
            avatarRes = R.drawable.img_profile_avatar_1794230085674,
            isLoggedIn = true,
            safetyRank = "Hiệp sĩ An toàn",
            levelScore = 1820,
            totalScans = 18,
            protectedHours = 240
        )
    )
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    private val _syncStatus = MutableStateFlow(SyncStatus.SYNCED)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()

    private val _lastSyncTimestamp = MutableStateFlow(System.currentTimeMillis())
    val lastSyncTimestamp: StateFlow<Long> = _lastSyncTimestamp.asStateFlow()

    private val _realtimeSyncEnabled = MutableStateFlow(true)
    val realtimeSyncEnabled: StateFlow<Boolean> = _realtimeSyncEnabled.asStateFlow()

    // Scan history state (loaded from Neon PostgreSQL with seed fallback)
    private val _scanHistory = MutableStateFlow<List<ScanResult>>(
        listOf(
            ScanResult(
                id = "scan-1",
                target = "https://vneid-xacthuc-taikhoan.vn/login.php?ref=otp",
                type = QueryType.URL,
                riskScore = 92,
                riskLevel = RiskLevel.HIGH_RISK,
                redFlags = listOf(
                    "Tên miền giả mạo Cổng Dịch vụ công VNeID",
                    "Yêu cầu cung cấp mã OTP và mật khẩu",
                    "Cấu trúc URL bất thường / Tên miền trôi nổi"
                ),
                recommendations = listOf(
                    "Không nhấp vào link, tuyệt đối không cung cấp thông tin cá nhân!",
                    "Chặn và báo cáo số điện thoại / nguồn gửi tin nhắn.",
                    "Liên hệ tổng đài VNeID chính thức 1900 0368 để kiểm tra nếu nghi ngờ."
                ),
                redFlagPoints = listOf(
                    RedFlagPoint("Tên miền giả mạo thương hiệu/dịch vụ công (vneid-xacthuc-taikhoan.vn)", 35),
                    RedFlagPoint("Yêu cầu nhập mã OTP hoặc dữ liệu tài khoản nhạy cảm", 30),
                    RedFlagPoint("Cấu trúc URL bất thường, sử dụng tên miền trôi nổi", 27)
                ),
                analyzedDomain = "vneid-xacthuc-taikhoan.vn",
                isDomainValid = false,
                timestamp = System.currentTimeMillis() - 15 * 60 * 1000
            )
        )
    )
    val scanHistory: StateFlow<List<ScanResult>> = _scanHistory.asStateFlow()

    // Real-time alerts feed (loaded from Neon PostgreSQL with seed fallback)
    private val _alerts = MutableStateFlow<List<SecurityAlert>>(
        listOf(
            SecurityAlert(
                id = "alert-1",
                title = "Cảnh giác với lừa đảo học bổng trực tuyến!",
                summary = "Các đối tượng tạo fanpage mạo danh học bổng quốc tế, yêu cầu nộp lệ phí cam kết.",
                tag = "Khẩn cấp",
                timeAgo = "5 phút trước",
                isUrgent = true,
                isRead = false
            ),
            SecurityAlert(
                id = "alert-2",
                title = "Cảnh báo Deepfake gọi video lừa chuyển tiền",
                summary = "Thủ đoạn giả mạo khuôn mặt bạn bè, người thân qua video call kém chất lượng để mượn tiền gấp.",
                tag = "Deepfake AI",
                timeAgo = "32 phút trước",
                isUrgent = true,
                isRead = false
            )
        )
    )
    val alerts: StateFlow<List<SecurityAlert>> = _alerts.asStateFlow()

    // Privacy checklist items
    private val _privacyItems = MutableStateFlow<List<PrivacyAuditItem>>(
        listOf(
            PrivacyAuditItem(
                id = "priv-1",
                title = "Xác thực hai yếu tố (2FA)",
                description = "Bảo vệ tài khoản bằng mã sinh trắc học hoặc ứng dụng Authenticator thay vì SMS.",
                isSecure = true,
                actionText = "Đang kích hoạt"
            ),
            PrivacyAuditItem(
                id = "priv-2",
                title = "Quyền riêng tư mạng xã hội",
                description = "Ẩn danh sách bạn bè, số điện thoại, ngày sinh công khai trên Facebook & Zalo.",
                isSecure = true,
                actionText = "Đã tối ưu"
            ),
            PrivacyAuditItem(
                id = "priv-3",
                title = "Ứng dụng truy cập Micro & Camera ngầm",
                description = "Kiểm tra và thu hồi quyền các ứng dụng lạ yêu cầu truy cập khi không mở ứng dụng.",
                isSecure = false,
                actionText = "Cần kiểm tra"
            ),
            PrivacyAuditItem(
                id = "priv-4",
                title = "Cảnh giác mật khẩu trùng lặp",
                description = "Không dùng chung 1 mật khẩu cho cả email chính, mạng xã hội và ngân hàng.",
                isSecure = true,
                actionText = "An toàn"
            )
        )
    )
    val privacyItems: StateFlow<List<PrivacyAuditItem>> = _privacyItems.asStateFlow()

    // Contextual Help Guides for each screen ('?' button)
    val helpGuides = mapOf(
        Screen.HOME to HelpGuide(
            title = "Hướng dẫn Khám phá TrustNet",
            subtitle = "Không gian số an toàn bắt đầu từ bạn",
            overview = "TrustNet cung cấp bộ giải pháp an toàn số đa lớp cho học sinh và gia đình. Hãy sử dụng các giải pháp bên dưới để bảo vệ danh tính số của bạn.",
            tips = listOf(
                "Kiểm tra nhanh mọi đường link hoặc tin nhắn lạ bằng TrustCheck trước khi nhấp vào.",
                "Rà soát quyền ứng dụng định kỳ trong TrustPrivacy để không bị nghe lén.",
                "Khi gặp khó khăn tâm lý hoặc bị đe dọa, kết nối ngay với chuyên gia TrustCare qua hotline 1800 96 96 12."
            ),
            quickActionTitle = "Khởi chạy quét bảo mật nhanh",
            hotline = "1800 96 96 12"
        ),
        Screen.TRUST_CHECK to HelpGuide(
            title = "Hướng dẫn Kiểm tra An toàn Số",
            subtitle = "Phát hiện nguy cơ lừa đảo, mã độc và tin giả",
            overview = "Công cụ ứng dụng mô hình AI Gemini và phân tích heuristic để bóc tách các dấu hiệu lừa đảo trong đường dẫn (URL), tin nhắn SMS hoặc số điện thoại.",
            tips = listOf(
                "Các liên kết giả mạo thường dùng tên miền lạ (đuôi .xyz, .top, .vip) hoặc tên miền rút gọn.",
                "Không bao giờ cung cấp mã OTP, mật khẩu hay số căn cước công dân vào bất kỳ trang web nào gửi qua tin nhắn.",
                "Điểm rủi ro từ 70/100 trở lên là mức báo động đỏ, bạn cần chặn nguồn gửi ngay lập tức."
            ),
            quickActionTitle = "Báo cáo đường dẫn độc hại",
            hotline = "1800 96 96 12"
        ),
        Screen.TRUST_PRIVACY to HelpGuide(
            title = "Hướng dẫn Bảo vệ Quyền Riêng Tư",
            subtitle = "Bảo mật dữ liệu cá nhân trên không gian mạng",
            overview = "Bảo vệ thông tin cá nhân giúp bạn ngăn ngừa các cuộc gọi rác, tin nhắn tống tiền và mạo danh lừa đảo.",
            tips = listOf(
                "Bật xác thực 2 lớp (2FA) bằng Google Authenticator cho Facebook, Google và Telegram.",
                "Đặt chế độ bạn bè hoặc chỉ mình tôi cho danh sách người theo dõi và ngày tháng năm sinh.",
                "Thường xuyên kiểm tra quyền 'Trợ năng' và 'Thông báo' trên điện thoại."
            ),
            quickActionTitle = "Kiểm tra rò rỉ dữ liệu cá nhân"
        ),
        Screen.TRUST_CARE to HelpGuide(
            title = "Hướng dẫn Phòng Chống Bạo Lực Mạng",
            subtitle = "Ứng xử văn minh và hỗ trợ tâm lý học đường",
            overview = "Khi bị bạo lực mạng, tống tiền hoặc phát tán hình ảnh riêng tư, bạn luôn có sự đồng hành và bảo vệ pháp lý.",
            tips = listOf(
                "Chụp lại toàn bộ màn hình làm bằng chứng (kèm ngày giờ và link trang cá nhân kẻ xấu).",
                "Tuyệt đối không chuyển tiền theo yêu cầu tống tiền của kẻ gian.",
                "Liên hệ ngay Tổng đài Quốc gia Bảo vệ Trẻ em 111 hoặc TrustCare 1800 96 96 12."
            ),
            quickActionTitle = "Gọi tổng đài tư vấn khẩn cấp",
            hotline = "1800 96 96 12"
        ),
        Screen.TRUST_AI to HelpGuide(
            title = "Hướng dẫn Nhận diện Deepfake & AI",
            subtitle = "Phát hiện nội dung giả mạo và video hoán đổi khuôn mặt",
            overview = "Deepfake sử dụng trí tuệ nhân tạo để mô phỏng giọng nói và khuôn mặt nhằm mục đích lừa chuyển tiền hoặc bôi nhọ.",
            tips = listOf(
                "Quan sát nhịp chớp mắt và cử động viền môi: Deepfake thường có độ trễ hoặc nhòe hình khi đối tượng quay đầu.",
                "Yêu cầu đối phương vẫy tay trước mặt hoặc quay mặt sang trái/phải 90 độ.",
                "Gọi điện kiểm chứng lại qua sóng di động truyền thống (SIM thường)."
            ),
            quickActionTitle = "Phân tích ảnh hoặc video nghi vấn"
        ),
        Screen.TRUST_LAB to HelpGuide(
            title = "Hướng dẫn Luyện tập TrustLab",
            subtitle = "Học qua tình huống thực tế và nhận huy hiệu an toàn",
            overview = "Các bài kiểm tra tương tác giúp bạn hình thành phản xạ tự nhiên khi gặp bẫy lừa đảo trên Internet.",
            tips = listOf(
                "Đọc kỹ tình huống và các dấu hiệu bất thường trước khi chọn đáp án.",
                "Mỗi câu trả lời đúng được cộng 100 điểm thưởng vào hồ sơ bảo mật của bạn."
            )
        ),
        Screen.TRUST_HELP to HelpGuide(
            title = "Tổng Đài & Đường Dây Nóng Khẩn Cấp",
            subtitle = "Các đầu mối tiếp nhận và xử lý sự cố an toàn thông tin",
            overview = "Danh bạ các cơ quan nhà nước và tổ chức bảo vệ quyền công dân trên không gian mạng.",
            tips = listOf(
                "Tổng đài Quốc gia 111: Tiếp nhận và xử lý khẩn cấp các vụ việc xâm hại trẻ em trên mạng.",
                "Cục An toàn thông tin (A05): Tiếp nhận tố giác tội phạm lừa đảo công nghệ cao.",
                "Đội phản ứng nhanh TrustNet: Hỗ trợ gỡ bỏ nội dung bôi nhọ và tư vấn tâm lý."
            ),
            hotline = "1800 96 96 12"
        ),
        Screen.TRUST_MEDIA to HelpGuide(
            title = "Cổng Thông Tin An Toàn Số",
            subtitle = "Cập nhật thủ đoạn lừa đảo mới nhất",
            overview = "Được biên soạn bởi Đội Xung Kích Số và các chuyên gia an ninh mạng hàng đầu Việt Nam."
        ),
        Screen.VOLUNTEER_TEAM to HelpGuide(
            title = "Đội Xung Kích Số Học Đường",
            subtitle = "Lan tỏa kiến thức số - Vì cộng đồng văn minh",
            overview = "Tham gia cùng hàng nghìn học sinh, sinh viên trên toàn quốc tuyên truyền an toàn không gian mạng."
        ),
        Screen.PROFILE to HelpGuide(
            title = "Quản lý Hồ sơ & Thiết lập Bảo mật",
            subtitle = "Đồng bộ đám mây và tùy chỉnh giao diện",
            overview = "Theo dõi cấp độ bảo mật, lịch sử quét và thiết lập liên kết cơ sở dữ liệu Neon PostgreSQL cùng mô hình Gemini AI."
        )
    )

    // Care Situations
    val careSituations = listOf(
        CareSituationItem(
            id = "care-1",
            title = "Bị bắt nạt, xúc phạm trên mạng",
            subtitle = "Nhận tin nhắn thù ghét, bị công kích tập thể hoặc lập nhóm nói xấu trên MXH.",
            adviceList = listOf(
                "Bước 1: Không đáp trả hoặc công kích lại kẻ bắt nạt. Giữ bình tĩnh tối đa.",
                "Bước 2: Chụp ảnh toàn bộ màn hình tin nhắn, bài viết, trang cá nhân làm bằng chứng (kèm ngày giờ).",
                "Bước 3: Sử dụng tính năng chặn (Block) và Báo cáo (Report) vi phạm tới quản trị viên nền tảng.",
                "Bước 4: Chia sẻ ngay với cha mẹ, thầy cô giáo phụ trách hoặc chuyên viên tâm lý trường học."
            ),
            emergencyNote = "Bạn không hề đơn độc! Hãy tâm sự với người lớn đáng tin cậy hoặc gọi tổng đài miễn phí."
        ),
        CareSituationItem(
            id = "care-2",
            title = "Bị đe dọa, tống tiền",
            subtitle = "Kẻ xấu dọa rò rỉ thông tin cá nhân, tống tiền bằng hình ảnh hoặc thông tin mật.",
            adviceList = listOf(
                "Bước 1: Tuyệt đối KHÔNG chuyển tiền theo yêu cầu. Việc chuyển tiền chỉ khiến kẻ xấu tống tiền nhiều hơn.",
                "Bước 2: Giữ nguyên lịch sử trò chuyện, tài khoản ngân hàng kẻ xấu cung cấp, ghi âm cuộc gọi.",
                "Bước 3: Tạm thời khóa trang cá nhân và chuyển chế độ riêng tư cao nhất.",
                "Bước 4: Báo ngay cho phụ huynh và cơ quan Công an an ninh mạng (Hotline A05: 069 234 8560)."
            ),
            emergencyNote = "Đừng giấu kín sợ hãi một mình. Báo cáo cơ quan chức năng là giải pháp dứt điểm an toàn nhất."
        ),
        CareSituationItem(
            id = "care-3",
            title = "Bị phát tán hình ảnh riêng tư",
            subtitle = "Hình ảnh cá nhân bị đăng tải trái phép, ghép ảnh bôi nhọ danh dự.",
            adviceList = listOf(
                "Bước 1: Báo cáo vi phạm quyền riêng tư trực tiếp tới nền tảng (Facebook, TikTok, Telegram) để gỡ bài khẩn cấp.",
                "Bước 2: Gửi yêu cầu gỡ bỏ tới Mạng lưới ứng cứu bảo vệ trẻ em trên không gian mạng qua Tổng đài 111.",
                "Bước 3: Yêu cầu bạn bè hỗ trợ cùng báo cáo đường link vi phạm để kích hoạt cơ chế gỡ tự động.",
                "Bước 4: Liên hệ tư vấn pháp lý và chuyên gia tâm lý học đường TrustNet."
            ),
            emergencyNote = "Phát tán hình ảnh cá nhân không có sự đồng ý là hành vi vi phạm pháp luật hình sự nghiêm trọng."
        ),
        CareSituationItem(
            id = "care-4",
            title = "Cần chia sẻ, tìm hỗ trợ tâm lý",
            subtitle = "Cảm thấy căng thẳng, lo âu, mất ngủ vì áp lực thế giới ảo.",
            adviceList = listOf(
                "Đặt điện thoại sang một bên, tạm ngắt kết nối mạng xã hội trong 24 giờ (Digital Detox).",
                "Thực hiện bài tập hít thở sâu 4-7-8 giúp xoa dịu hệ thần kinh.",
                "Trò chuyện cởi mở với bạn bè thân thiết ngoài đời thực.",
                "Kết nối với chuyên gia tâm lý học đường qua đường dây nóng miễn phí TrustCare 1800 96 96 12."
            ),
            emergencyNote = "Đội ngũ chuyên viên tâm lý TrustNet sẵn sàng lắng nghe bạn 24/7 một cách bảo mật tuyệt đối."
        )
    )

    // Quiz Questions for TrustLab
    val quizQuestions = listOf(
        QuizQuestion(
            id = 1,
            title = "Tin nhắn trúng thưởng bất ngờ",
            situationText = "Bạn nhận được tin nhắn SMS: 'Chúc mừng bạn trúng học bổng toàn phần và phần thưởng 10.000.000đ. Nhấp vào link bit.ly/hocbong-2026 và nhập OTP để nhận tiền ngay trong 30 phút'. Bạn sẽ làm gì?",
            options = listOf(
                "A. Nhấp vào link và nhập thông tin để không bỏ lỡ học bổng",
                "B. Không nhấp vào link, chụp màn hình gửi TrustNet kiểm tra và cảnh báo bạn bè",
                "C. Chuyển tiếp tin nhắn cho người thân nhờ họ nhận hộ",
                "D. Nhắn tin lại số điện thoại đó hỏi xem văn phòng ở đâu"
            ),
            correctIndex = 1,
            explanation = "Chính xác! Các thông báo kèm đường link rút gọn (bit.ly, tinyurl...) và hối thúc nhập mã OTP đều là dấu hiệu lừa đảo kinh điển. Tuyệt đối không nhấp link và không cung cấp OTP cho bất kỳ ai!"
        ),
        QuizQuestion(
            id = 2,
            title = "Cuộc gọi video Deepfake",
            situationText = "Bạn nhận cuộc gọi video từ tài khoản của một người bạn thân. Hình ảnh giật nhẹ, khẩu hình miệng không khớp tiếng, người bạn hối hả nhờ bạn chuyển khoản gấp 2 triệu để thanh toán viện phí vì tài khoản đang lỗi. Bạn làm gì?",
            options = listOf(
                "A. Chuyển tiền ngay vì đã nhìn thấy mặt bạn mình",
                "B. Cúp máy ngay và gọi điện thoại trực tiếp qua SIM thường hoặc hỏi câu hỏi bí mật",
                "C. Nhắn tin hỏi số tài khoản rồi chuyển một nửa tiền",
                "D. Đăng lên Facebook hỏi mọi người xem bạn ấy có bị ốm thật không"
            ),
            correctIndex = 1,
            explanation = "Xuất sắc! Kẻ gian dùng công nghệ Deepfake hoán đổi khuôn mặt và nhại giọng nói. Để kiểm chứng, hãy gọi điện trực tiếp qua sóng di động thông thường hoặc hỏi những thông tin chỉ hai bạn biết."
        ),
        QuizQuestion(
            id = 3,
            title = "Yêu cầu quyền ứng dụng",
            situationText = "Bạn tải một ứng dụng 'Chỉnh sửa ảnh anime' trên trang web trôi nổi. Khi mở app, ứng dụng yêu cầu cấp quyền: 'Đọc và gửi tin nhắn SMS', 'Truy cập danh bạ', 'Quyền trợ năng (Accessibility)'. Bạn xử lý sao?",
            options = listOf(
                "A. Cấp toàn bộ quyền để app chạy mượt mà",
                "B. Từ chối cấp quyền lạ và gỡ cài đặt ứng dụng ngay lập tức",
                "C. Chỉ từ chối quyền danh bạ, còn lại cho phép",
                "D. Cấp quyền rồi tắt mạng đi"
            ),
            correctIndex = 1,
            explanation = "Rất chuẩn! Một ứng dụng chỉnh sửa ảnh không bao giờ cần quyền đọc tin nhắn SMS hay Accessibility. Đây là ứng dụng chứa mã độc gián điệp nhằm đánh cắp mã OTP giao dịch ngân hàng."
        )
    )

    // Media Articles
    val mediaArticles = listOf(
        MediaArticle(
            id = "art-1",
            title = "Cảnh giác với lừa đảo học bổng trực tuyến!",
            category = "Góc cảnh báo",
            date = "08/11/2026",
            readMinutes = "4 phút đọc",
            summary = "Nhận diện 5 chiêu trò tinh vi dụ dỗ học sinh THPT và sinh viên đóng phí giữ chỗ học bổng quốc tế giả mạo.",
            content = "Thời gian gần đây, Trung tâm An toàn không gian mạng TrustNet liên tục ghi nhận nhiều phản ánh về các fanpage giả danh tổ chức giáo dục quốc tế. Thủ đoạn phổ biến là gửi email thông báo trúng học bổng 80-100%, yêu cầu học sinh chuyển khoản từ 2 - 5 triệu đồng tiền 'phí xét duyệt bảo lưu'. Khi học sinh chuyển tiền, các đối tượng lập tức chặn liên lạc và biến mất.\n\nLời khuyên: Mọi chương trình học bổng uy tín không bao giờ yêu cầu nộp lệ phí cam kết qua tài khoản cá nhân. Hãy kiểm tra thông tin qua thầy cô và website chính thống của trường.",
            isAlert = true
        ),
        MediaArticle(
            id = "art-2",
            title = "Bảo vệ tài khoản cá nhân trước làn sóng tấn công chiếm đoạt Telegram, Facebook",
            category = "Cẩm nang",
            date = "07/11/2026",
            readMinutes = "6 phút đọc",
            summary = "Hướng dẫn chi tiết thiết lập xác thực 2 bước, kiểm tra phiên đăng nhập và tạo mã khóa bảo vệ.",
            content = "Hiện tượng gửi tin nhắn bình chọn cuộc thi ảnh, vote tài năng để lừa quét mã QR đăng nhập tài khoản đang gia tăng. Khi quét mã QR hoặc nhập OTP, bạn đã vô tình chuyển giao quyền điều khiển tài khoản cho kẻ gian.\n\nHãy kích hoạt ngay tính năng Two-Step Verification trong Cài đặt bảo mật, đặt mật khẩu thứ hai và kiểm tra mục 'Thiết bị đang đăng nhập' để đăng xuất khỏi các thiết bị lạ.",
            isAlert = false
        ),
        MediaArticle(
            id = "art-3",
            title = "Nhận diện Deepfake 2026: Đôi mắt và ánh sáng không biết nói dối",
            category = "Tin tức",
            date = "06/11/2026",
            readMinutes = "5 phút đọc",
            summary = "Những mẹo thị giác đơn giản giúp bạn phát hiện video và hình ảnh do AI tạo sinh chỉ trong 10 giây.",
            content = "Dù AI ngày càng tinh vi, các video Deepfake thời gian thực vẫn để lộ những nhược điểm vật lý: nhịp chớp mắt không tự nhiên, bóng đổ dưới cằm và mũi không đồng nhất với nguồn sáng xung quanh, hoặc vành tai bị nhòe pixel.\n\nKhi nghi ngờ, hãy yêu cầu đối phương quay nghiêng mặt 90 độ hoặc đưa bàn tay qua lại trước mặt. Thuật toán AI thường sẽ bị vỡ hình ngay lập tức.",
            isAlert = false
        ),
        MediaArticle(
            id = "art-4",
            title = "Cùng nâng cao kỹ năng an toàn số: Cẩm nang dành cho học sinh THPT",
            category = "Cẩm nang",
            date = "05/11/2026",
            readMinutes = "8 phút đọc",
            summary = "Bộ quy tắc ứng xử văn minh và bảo vệ dữ liệu cá nhân trên không gian số do Đội Xung Kích Số biên soạn.",
            content = "Không gian số là một phần mở rộng của cuộc sống thực tế. Mỗi học sinh cần trang bị 'tư duy phòng vệ số': Không chia sẻ vị trí thời gian thực, không gửi ảnh nhạy cảm cho bất kỳ ai, tôn trọng quyền riêng tư của bạn bè và luôn biết cách tìm kiếm sự trợ giúp từ người lớn khi gặp sự cố.",
            isAlert = false
        )
    )

    // Volunteer Team Activities
    val volunteerActivities = listOf(
        VolunteerActivity(
            id = "vol-1",
            title = "Hội thảo: An toàn số học đường 2026",
            date = "15/11/2026 - 08:30",
            location = "THPT Chuyên Lê Hồng Phong",
            participants = "120 học sinh tham gia",
            description = "Trang bị kỹ năng nhận diện lừa đảo trực tuyến và phòng chống bạo lực mạng cho học sinh khối 10 & 11."
        ),
        VolunteerActivity(
            id = "vol-2",
            title = "Chiến dịch: Lan tỏa cẩm nang 'Lá chắn số'",
            date = "22/11/2026 - 14:00",
            location = "Phát động trực tuyến toàn quốc",
            participants = "850+ tình nguyện viên",
            description = "Tuyên truyền các infographic phòng chống Deepfake tới hơn 50.000 học sinh các trường THPT."
        ),
        VolunteerActivity(
            id = "vol-3",
            title = "Workshop: Thực hành phân tích đường link độc hại",
            date = "29/11/2026 - 09:00",
            location = "Phòng Lab Tin học THPT Chu Văn An",
            participants = "65 thành viên xung kích",
            description = "Tập huấn chuyên sâu kỹ thuật nhận diện URL phishing, domain spoofing và hỗ trợ người dùng ứng dụng TrustNet."
        )
    )

    init {
        scope.launch {
            loadFromNeon()
            startRealtimeSyncLoop()
        }
    }

    private suspend fun loadFromNeon() {
        try {
            val dbScans = neonClient.getScanHistory()
            if (dbScans.isNotEmpty()) {
                _scanHistory.value = dbScans
            }

            val dbAlerts = neonClient.getSecurityAlerts()
            if (dbAlerts.isNotEmpty()) {
                _alerts.value = dbAlerts
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load initial data from Neon: ${e.message}")
        }
    }

    private fun startRealtimeSyncLoop() {
        scope.launch {
            while (true) {
                delay(25_000)
                if (_realtimeSyncEnabled.value) {
                    try {
                        val freshAlerts = neonClient.getSecurityAlerts()
                        if (freshAlerts.isNotEmpty()) {
                            _alerts.value = freshAlerts
                        }
                        _lastSyncTimestamp.value = System.currentTimeMillis()
                    } catch (e: Exception) {
                        Log.w(TAG, "Realtime sync loop exception: ${e.message}")
                    }
                }
            }
        }
    }

    suspend fun triggerManualSync(): Boolean {
        _syncStatus.value = SyncStatus.SYNCING
        try {
            val freshScans = neonClient.getScanHistory()
            if (freshScans.isNotEmpty()) _scanHistory.value = freshScans

            val freshAlerts = neonClient.getSecurityAlerts()
            if (freshAlerts.isNotEmpty()) _alerts.value = freshAlerts

            _lastSyncTimestamp.value = System.currentTimeMillis()
            _syncStatus.value = SyncStatus.SYNCED
            return true
        } catch (e: Exception) {
            _syncStatus.value = SyncStatus.OFFLINE
            return false
        }
    }

    fun toggleRealtimeSync(enabled: Boolean) {
        _realtimeSyncEnabled.value = enabled
        _syncStatus.value = if (enabled) SyncStatus.SYNCED else SyncStatus.OFFLINE
    }

    fun togglePrivacyAuditItem(id: String) {
        _privacyItems.value = _privacyItems.value.map { item ->
            if (item.id == id) item.copy(isSecure = !item.isSecure) else item
        }
    }

    suspend fun login(email: String, password: String):Boolean {
        return try {
            val user = neonClient.authenticateUser(email, password)
            if (user != null) {
                _currentUser.value = user.copy(avatarRes = R.drawable.img_profile_avatar_1794230085674)
                true
            } else {
                // Fallback login for seamless offline/first demo
                _currentUser.value = UserProfile(
                    name = email.substringBefore("@").replace(".", " ").capitalizeWords(),
                    email = email,
                    avatarRes = R.drawable.img_profile_avatar_1794230085674,
                    isLoggedIn = true
                )
                true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Login exception: ${e.message}")
            false
        }
    }

    suspend fun register(name: String, email: String, password: String): Boolean {
        return try {
            val user = neonClient.registerUser(name, email, password)
            if (user != null) {
                _currentUser.value = user.copy(avatarRes = R.drawable.img_profile_avatar_1794230085674)
                true
            } else {
                _currentUser.value = UserProfile(
                    name = name,
                    email = email,
                    avatarRes = R.drawable.img_profile_avatar_1794230085674,
                    isLoggedIn = true
                )
                true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Register exception: ${e.message}")
            false
        }
    }

    fun loginWithGoogle(): Boolean {
        _currentUser.value = UserProfile(
            name = "Amelia Andra",
            email = "amelia.andra@gmail.com",
            avatarRes = R.drawable.img_profile_avatar_1794230085674,
            isLoggedIn = true
        )
        return true
    }

    fun logout() {
        _currentUser.value = UserProfile(
            name = "Khách vãng lai",
            email = "chua-dang-nhap@trustnet.org",
            avatarRes = null,
            isLoggedIn = false,
            totalScans = 0,
            levelScore = 0
        )
    }

    suspend fun analyzeTarget(query: String, type: QueryType): ScanResult {
        val result = aiService.analyzeContentWithGemini(query, type)

        _scanHistory.value = listOf(result) + _scanHistory.value

        // Increment scan count in profile
        _currentUser.value = _currentUser.value.copy(
            totalScans = _currentUser.value.totalScans + 1,
            levelScore = _currentUser.value.levelScore + 10
        )

        scope.launch {
            try {
                neonClient.insertScanResult(result)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to persist scan to Neon: ${e.message}")
            }
        }

        return result
    }

    suspend fun getCareAdvice(situation: String): CareAiAdvice {
        return aiService.getCareGuidance(situation)
    }

    suspend fun analyzeDeepfake(
        fileName: String,
        isSimulatedFake: Boolean = false,
        customApiUrl: String = ""
    ): DeepfakeAnalysisResult {
        return aiService.detectAiMedia(fileName, isSimulatedFake, customApiUrl)
    }

    suspend fun submitIncidentReport(target: String, details: String): Boolean {
        return neonClient.insertIncidentReport(target, details)
    }

    suspend fun submitVolunteerRegistration(name: String, school: String, phone: String): Boolean {
        return neonClient.registerVolunteer(name, school, phone)
    }

    fun markAlertAsRead(id: String) {
        _alerts.value = _alerts.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }

    fun clearHistory() {
        _scanHistory.value = emptyList()
        scope.launch {
            try {
                neonClient.clearHistory()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to clear Neon history: ${e.message}")
            }
        }
    }

    private fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
}
