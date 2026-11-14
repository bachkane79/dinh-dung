package com.example.data

import com.example.model.PrivacyAuditItem

internal val privacyChecklist = buildList {
    fun task(id: String, group: String, title: String, detail: String) {
        add(PrivacyAuditItem(id, title, detail, false, group))
    }

    val accounts = "Mật khẩu và đăng nhập"
    task("unique-passwords", accounts, "Dùng mật khẩu riêng cho từng tài khoản", "Ưu tiên email chính, mạng xã hội và tài khoản khôi phục.")
    task("password-manager", accounts, "Dùng trình quản lý mật khẩu", "Tạo mật khẩu dài và ngẫu nhiên. Bảo vệ kho mật khẩu bằng mật khẩu chính riêng.")
    task("passkeys", accounts, "Bật passkey hoặc khóa bảo mật", "Dùng trên các dịch vụ hỗ trợ. Kiểm tra thiết bị dự phòng trước khi bỏ cách đăng nhập cũ.")
    task("authenticator", accounts, "Bật xác thực hai bước", "Ưu tiên ứng dụng xác thực hoặc khóa bảo mật. Lưu mã dự phòng ở nơi an toàn.")
    task("recovery-codes", accounts, "Cất mã khôi phục riêng", "Giữ bản in hoặc bản mã hóa tách khỏi điện thoại dùng để nhận mã xác thực.")
    task("recovery-email", accounts, "Kiểm tra email và số khôi phục", "Xóa địa chỉ và số cũ. Bảo vệ email khôi phục bằng xác thực hai bước.")
    task("login-notifications", accounts, "Bật cảnh báo đăng nhập mới", "Mở trực tiếp ứng dụng để xác minh nếu nhận cảnh báo lạ.")
    task("active-sessions", accounts, "Rà soát phiên đăng nhập", "Đăng xuất thiết bị không nhận ra hoặc máy tính dùng chung.")
    task("linked-apps", accounts, "Gỡ ứng dụng đã liên kết mà không còn dùng", "Kiểm tra mục Ứng dụng và trang web trong phần bảo mật của từng tài khoản.")
    task("mail-rules", accounts, "Kiểm tra chuyển tiếp và bộ lọc email", "Xóa quy tắc lạ tự chuyển tiếp thư hoặc tự ẩn thông báo bảo mật.")
    task("otp", accounts, "Giữ riêng mã OTP và mã đăng nhập", "Từ chối yêu cầu đọc mã, gửi ảnh mã hoặc quét QR đăng nhập từ người lạ.")
    task("account-recovery", accounts, "Lưu trang khôi phục tài khoản chính thức", "Dùng dấu trang hoặc ứng dụng chính thức để tránh trang hỗ trợ giả.")
    task("old-accounts", accounts, "Đóng tài khoản cũ", "Tải dữ liệu cần giữ, gỡ liên kết với tài khoản chính rồi yêu cầu xóa.")

    val social = "Mạng xã hội và nhắn tin"
    task("audience", social, "Chọn người xem bài viết mặc định", "Đặt theo đối tượng bạn thực sự muốn chia sẻ và kiểm tra từng bài trước khi đăng.")
    task("old-posts", social, "Rà soát bài viết công khai cũ", "Giới hạn bài cũ chứa địa chỉ, trường học, lịch trình hoặc thông tin gia đình.")
    task("profile", social, "Ẩn số điện thoại, email và ngày sinh", "Xem thử hồ sơ ở chế độ công khai để kiểm tra thông tin còn lộ.")
    task("friends", social, "Giới hạn người xem danh sách bạn bè", "Rà soát người theo dõi và gỡ tài khoản đáng ngờ.")
    task("lookup", social, "Giới hạn tìm kiếm bằng số điện thoại", "Kiểm tra cả tùy chọn tìm bằng email hoặc đồng bộ danh bạ.")
    task("search-engine", social, "Kiểm tra khả năng tìm hồ sơ qua công cụ tìm kiếm", "Tắt liên kết từ công cụ tìm kiếm nếu muốn giảm khả năng bị tìm thấy.")
    task("tag-review", social, "Bật duyệt bài gắn thẻ", "Kiểm tra bài trước khi cho xuất hiện trên trang cá nhân.")
    task("messages", social, "Hạn chế tin nhắn và cuộc gọi từ người lạ", "Điều chỉnh mục yêu cầu tin nhắn, nhóm và lời mời theo nhu cầu.")
    task("comments", social, "Giới hạn bình luận và nhắc tên", "Bật bộ lọc từ khóa hoặc hạn chế tài khoản thường xuyên quấy rối.")
    task("location", social, "Rà soát quyền vị trí của ứng dụng xã hội", "Tắt vị trí chính xác và chia sẻ vị trí trực tiếp khi không cần.")
    task("photos", social, "Kiểm tra ảnh trước khi đăng", "Che giấy tờ, vé, mã QR, địa chỉ và chi tiết nhận diện ở hậu cảnh.")
    task("geo-tags", social, "Kiểm tra vị trí gắn trong ảnh", "Xóa vị trí hoặc gửi bản đã bỏ dữ liệu vị trí khi chia sẻ công khai.")
    task("live-sharing", social, "Trì hoãn đăng thông tin chuyến đi", "Tránh công khai vị trí hiện tại hoặc thời điểm nhà không có người.")
    task("member-check", social, "Rà soát thành viên nhóm và liên kết mời", "Xóa liên kết mời cũ nếu nhóm chứa thông tin riêng.")
    task("verify-request", social, "Xác minh yêu cầu vay tiền qua kênh khác", "Gọi lại số đã biết khi bạn bè nhắn nhờ chuyển tiền hoặc gửi mã.")
    task("report-block", social, "Biết cách chặn và báo cáo", "Lưu bằng chứng cần thiết trước khi chặn tài khoản mạo danh hoặc quấy rối.")
    task("encrypted-chat", social, "Kiểm tra bảo mật cuộc trò chuyện riêng tư", "Ưu tiên mã hóa đầu cuối khi dịch vụ hỗ trợ và kiểm tra cách sao lưu tin nhắn.")
    task("contact-sync", social, "Rà soát đồng bộ danh bạ", "Tắt đồng bộ nếu không dùng và kiểm tra tùy chọn xóa danh bạ đã tải lên.")

    val computer = "Máy tính và thiết bị cá nhân"
    task("os-update", computer, "Bật cập nhật hệ điều hành", "Cài bản vá và khởi động lại để hoàn tất cập nhật.")
    task("supported-os", computer, "Kiểm tra hệ điều hành còn được hỗ trợ", "Lên kế hoạch nâng cấp nếu thiết bị đã hết nhận bản vá.")
    task("apps-update", computer, "Cập nhật trình duyệt và ứng dụng", "Chú ý ứng dụng văn phòng, đọc PDF, nhắn tin và truy cập từ xa.")
    task("screen-lock", computer, "Bật khóa màn hình tự động", "Dùng PIN hoặc mật khẩu đủ mạnh và khóa màn hình khi rời máy.")
    task("disk-encryption", computer, "Bật mã hóa ổ đĩa", "Dùng BitLocker, Device Encryption hoặc FileVault và cất khóa khôi phục riêng.")
    task("standard-user", computer, "Dùng tài khoản thường cho công việc hằng ngày", "Chỉ cấp quyền quản trị khi cần cài đặt hoặc đổi cấu hình hệ thống.")
    task("antivirus", computer, "Bật bảo vệ chống mã độc", "Kiểm tra bảo vệ thời gian thực và cập nhật dữ liệu nhận diện.")
    task("firewall", computer, "Bật tường lửa", "Kiểm tra cấu hình cho mạng riêng lẫn mạng công cộng.")
    task("installed-software", computer, "Gỡ phần mềm và tiện ích không còn dùng", "Rà soát danh sách phần mềm, ứng dụng khởi động cùng máy và tiện ích trình duyệt.")
    task("official-downloads", computer, "Cài phần mềm từ nguồn chính thức", "Kiểm tra nhà phát hành trước khi cấp quyền cài đặt hoặc chạy lệnh.")
    task("permissions", computer, "Rà soát quyền camera, micro và vị trí", "Thu hồi quyền ứng dụng không còn cần dùng và kiểm tra ứng dụng được chạy nền.")
    task("remote-access", computer, "Tắt truy cập từ xa khi không cần", "Kiểm tra Remote Desktop, chia sẻ màn hình và công cụ hỗ trợ từ xa.")
    task("file-sharing", computer, "Rà soát thư mục dùng chung", "Gỡ quyền khách và liên kết công khai không còn cần dùng.")
    task("browser-warnings", computer, "Giữ cảnh báo tải xuống và trang độc hại", "Kiểm tra kỹ tên miền khi trình duyệt báo lỗi chứng chỉ.")
    task("file-extensions", computer, "Hiện phần mở rộng tệp", "Kiểm tra đuôi như .exe, .msi, .bat và .js trước khi mở tệp nhận từ người khác.")
    task("macros", computer, "Giữ macro bị chặn với tài liệu lạ", "Xác minh người gửi qua kênh khác trước khi mở tài liệu yêu cầu bật macro.")
    task("usb", computer, "Chỉ dùng thiết bị USB tin cậy", "Không cắm USB nhặt được hoặc thiết bị không rõ nguồn gốc.")
    task("backup", computer, "Sao lưu dữ liệu quan trọng", "Giữ thêm bản sao ngoài máy. Tách ít nhất một bản sao khỏi thiết bị sau khi sao lưu.")
    task("restore", computer, "Thử khôi phục một tệp từ bản sao lưu", "Kiểm tra có thể mở tệp và biết nơi tìm khóa khôi phục.")
    task("device-finder", computer, "Bật tìm thiết bị nếu phù hợp", "Kiểm tra cách khóa máy và đăng xuất từ xa khi bị mất.")
    task("wipe-device", computer, "Xóa dữ liệu trước khi bán hoặc cho máy", "Sao lưu phần cần giữ, đăng xuất tài khoản rồi dùng quy trình đặt lại của hệ điều hành.")

    val network = "Wi-Fi và mạng gia đình"
    task("router-password", network, "Đổi mật khẩu quản trị router", "Dùng mật khẩu riêng và tắt quản trị qua Internet nếu không cần.")
    task("router-firmware", network, "Cập nhật phần mềm router", "Kiểm tra trang quản trị hoặc hướng dẫn chính thức của nhà mạng.")
    task("wifi-encryption", network, "Dùng WPA3 hoặc WPA2-AES", "Đặt mật khẩu Wi-Fi dài và đổi nếu đã chia sẻ cho người không còn cần truy cập.")
    task("guest-wifi", network, "Tách mạng khách và thiết bị thông minh", "Dùng mạng khách hoặc mạng riêng nếu router hỗ trợ.")
    task("wps", network, "Tắt WPS nếu không sử dụng", "Kết nối bằng mật khẩu Wi-Fi hoặc phương thức thiết lập bảo mật của thiết bị.")
    task("public-wifi", network, "Tắt tự động kết nối Wi-Fi công cộng", "Dùng dữ liệu di động cho thao tác nhạy cảm khi không chắc mạng có đáng tin.")
    task("router-devices", network, "Kiểm tra thiết bị đang kết nối", "Đổi mật khẩu Wi-Fi nếu thấy thiết bị lạ không giải thích được, rồi kết nối lại thiết bị của mình.")
}
