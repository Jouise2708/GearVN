# GearVN - Gaming Gear E-Commerce Platform

![Java](https://img.shields.io/badge/Java-100%25-red)
![License](https://img.shields.io/badge/License-MIT-green)

## Giới thiệu

**GearVN** là một nền tảng thương mại điện tử chuyên bán các thiết bị gaming chất lượng cao. Dự án bao gồm backend Java và frontend, cung cấp các tính năng quản lý sản phẩm, giỏ hàng, đơn hàng và tài khoản người dùng.

## Các sản phẩm

- **Laptop Gaming** - ASUS ROG, MSI, Acer Nitro, Lenovo Legion...
- **Chuột Gaming** - Razer, Logitech, Corsair, SteelSeries...
- **Bàn phím Cơ** - AULA, AKKO, Corsair, Logitech, Razer, DareU...
- **Tai nghe Gaming** - Razer, HyperX, Logitech, Corsair, SteelSeries...
- **Màn hình Gaming** - ASUS TUF, MSI, LG UltraGear, Samsung Odyssey...
- **Linh kiện PC** - CPU, GPU, RAM, SSD...

## Công nghệ sử dụng

- **Backend**: Java
- **Database**: SQL Server (T-SQL)
- **Frontend**: (Frontend framework - xem thêm trong thư mục `/frontend`)

## Cấu trúc dự án

```
GearVN/
├── backend/           # Mã nguồn backend Java
├── frontend/          # Ứng dụng frontend
├── gearvn.sql         # Schema database và dữ liệu mẫu
└── README.md          # File này
```

## Cơ sở dữ liệu

### Bảng chính

- **users**: Lưu thông tin người dùng (email, password, tên)
- **products**: Catalog sản phẩm gaming (tên, giá, mô tả)
- **cart_items**: Giỏ hàng của người dùng
- **orders**: Đơn hàng
- **order_items**: Chi tiết từng item trong đơn hàng

## Hướng dẫn cài đặt

### Yêu cầu

- Java JDK 8+
- SQL Server
- Maven (hoặc Gradle)

### Các bước cài đặt

1. **Clone repository**
   ```bash
   git clone https://github.com/Jouise2708/GearVN.git
   cd GearVN
   ```

2. **Setup Database**
   - Mở SQL Server Management Studio
   - Chạy file `gearvn.sql` để tạo database và bảng
   ```sql
   -- Mở file gearvn.sql trong SSMS hoặc chạy:
   sqlcmd -S your_server_name -i gearvn.sql
   ```

3. **Build Backend**
   ```bash
   cd backend
   mvn clean install
   mvn spring-boot:run
   ```

4. **Setup Frontend**
   ```bash
   cd frontend
   npm install
   npm start
   ```

## Tính năng chính

- Xem danh sách sản phẩm
- Tìm kiếm và lọc sản phẩm
- Giỏ hàng (thêm/xoá sản phẩm)
- Đặt hàng
- Quản lý đơn hàng
- Hệ thống tài khoản người dùng
- Xác thực người dùng (Login/Register)

## Bảo mật

- Mật khẩu được mã hóa bằng bcrypt
- Hỗ trợ xác thực người dùng

## Các trạng thái đơn hàng

- `PENDING`: Chờ xử lý
- `COMPLETED`: Hoàn thành
- `CANCELLED`: Huỷ bỏ

## Cộng tác

Chúng tôi hoan nghênh các pull request! Vui lòng:

1. Fork repository
2. Tạo branch feature (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open Pull Request

**Cảm ơn bạn đã sử dụng GearVN!**
