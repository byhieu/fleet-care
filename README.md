# Car Tycoon – Ông trùm cho thuê xe

Game quản lý cửa hàng cho thuê xe viết bằng Java (Swing). Bạn có **30 ngày**, **15.000.000 đ** và **2 chiếc xe cũ**:
cho khách thuê xe, rửa xe, sửa xe, mua thêm xe, lên cấp… và cuối cùng xem mình đạt **hạng S, A, B hay C**.

## 1. Chạy game

Chỉ cần **JDK 17 trở lên**. Không cần cài thêm gì khác.

| Việc | Windows | Linux / macOS |
|---|---|---|
| Chơi game | `run.bat` | `./run.sh` |
| Chạy unit test (JUnit 5) | `test.bat` | `./test.sh` |
| Chỉ biên dịch | `build.bat` | |

- Dùng IDE (IntelliJ, NetBeans, Eclipse): mở thư mục như **project Maven** (`pom.xml`). Main class: `com.cartycoon.app.Main`.
- Game tự lưu vào `save.dat` sau mỗi ngày; bảng xếp hạng nằm trong `highscores.txt` (cạnh chương trình).

## 2. Cách chơi

Mỗi **ngày là một lượt**, không có đồng hồ đếm giờ:

1. **Buổi sáng** có 4–8 khách đến, mỗi khách có một yêu cầu ("xe từ 7 chỗ", "chỉ thích xe thể thao"...).
   Đôi khi có **sự kiện bất ngờ**: mưa bão, phạt nguội, trúng thưởng, mùa du lịch...
2. **Trong ngày** bạn làm gì cũng được (miễn đủ tiền):
   chọn 1 khách bên trái + 1 xe bên phải → **Cho thuê** (khách trả tiền ngay), **Rửa xe**, **Sửa xe**, **Bán xe**, vào **Cửa hàng xe** mua xe mới.
   Cột *"Với khách chọn"* cho biết xe nào hợp và khách sẽ trả bao nhiêu.
3. Bấm **Ngày tiếp theo**: xe đang thuê chạy thêm một ngày (tăng km, giảm tình trạng); hết hạn thuê thì xe về bãi **bẩn**,
   đôi khi bị va quẹt; khách chấm điểm (uy tín lên hoặc xuống); khách chưa được phục vụ bỏ đi; trả phí bãi 100.000 đ/xe.
   **Tiền âm cuối ngày = phá sản!**

### Các loại xe (kế thừa từ lớp `Car`)

| Lớp | Xe | Chỗ | Giá mua | Cho thuê/ngày | Hao mòn/ngày | Mở khóa |
|---|---|---|---|---|---|---|
| `CityCar` | Kia Morning | 4 | 5 tr | 500.000 đ | 3% | Cấp 1 |
| `Sedan` | Toyota Vios | 5 | 7 tr | 800.000 đ | 3% | Cấp 1 |
| `Suv` | Toyota Innova | 7 | 10 tr | 1.200.000 đ | 4% | Cấp 1 |
| `Van` | Ford Transit | 16 | 15 tr | 1.800.000 đ | 5% | Cấp 2 |
| `SportsCar` | Mazda MX-5 | 2 | 25 tr | 3.000.000 đ | 8% (sửa đắt hơn 50%) | Cấp 3 |
| `LuxuryCar` | Mercedes S450 | 5 | 35 tr | 4.500.000 đ | 3% | Cấp 4 |

Xe dưới **30%** tình trạng là **hỏng**, phải sửa mới cho thuê được.
Rửa xe 100.000 đ. Sửa lên 100%: `(100 − tình trạng)% × giá mua × 0,3`. Bán xe: `giá mua × tình trạng% × 0,6`.

### Các loại khách (kế thừa từ lớp `Customer`)

| Lớp | Khách | Muốn gì | Trả | Rủi ro va quẹt | Xuất hiện từ |
|---|---|---|---|---|---|
| `StudentCustomer` | Sinh viên | xe tối đa 900.000 đ/ngày, không chê xe bẩn | ×0,8 | 20% | Cấp 1 |
| `FamilyCustomer` | Gia đình | xe từ 7 chỗ, bo 200.000 đ khi vui | ×1,0 | 5% | Cấp 1 |
| `TouristCustomer` | Đoàn du lịch | xe từ 16 chỗ, thuê 2–4 ngày | ×1,1 | 15% | Cấp 2 |
| `RacerCustomer` | Dân chơi | chỉ xe thể thao, làm xe hao mòn gấp đôi | ×1,5 | 40% | Cấp 3 |
| `BossCustomer` | Sếp lớn | sedan hoặc xe sang, sạch, từ 80%, bo 10% | ×1,3 | 5% | Cấp 3 |

Mỗi sáng luôn có **1 khách "nhử"** của cấp sau, để bạn muốn phát triển cửa hàng.
Khách vui (xe sạch, còn tốt) → uy tín +3; khách không vui → uy tín −5; khách bỏ đi → −1. Uy tín từ 80 → thêm khách mỗi ngày.

### Cấp, thành tích, điểm

- **Cấp** theo tổng tiền cho thuê: cấp 2 từ 15 triệu, cấp 3 từ 40 triệu, cấp 4 từ 80 triệu, cấp 5 ("Ông trùm") từ 150 triệu.
- **6 thành tích**: Khai trương, Khách quen (10 khách vui), Bãi xe đông vui (5 xe), Chơi lớn (mua xe thể thao), Đại gia (100 triệu), 5 sao (uy tín 100).
- **Hết 30 ngày**: điểm = tiền mặt + giá trị bán các xe + uy tín × 100.000 đ.
  **S** từ 100 triệu, **A** từ 70 triệu, **B** từ 45 triệu, còn lại **C**. 5 điểm cao nhất được lưu vào bảng xếp hạng.
- Cân bằng game đã được kiểm tra bằng test (`BalanceTest`): chơi chăm (mua thêm xe, giữ xe sạch) thường đạt A–S, không mua xe gì chỉ được B–C.

## 3. Cấu trúc code

```text
src/main/java/com/cartycoon
 |— app      Main
 |— model    Car + 6 lớp xe, Customer + 5 lớp khách, CarStatus, Rental, Level, Achievement, GameState
 |— logic    GameEngine (mọi luật chơi), CarCatalog (cửa hàng), CustomerFactory, GameListener,
 |           ScoreBoard (điểm, bảng xếp hạng), SaveManager (lưu game), GameException, DayReport
 |    |— event   RandomEvent + 7 sự kiện, EventFactory
 |— ui       StartWindow, MainWindow, CustomerPanel, GaragePanel, ShopDialog, DaySummaryDialog, GameOverDialog
 |— util     Format (tiền kiểu 12.500.000 đ)
src/test/java/com/cartycoon   unit test cho model, luật chơi, sự kiện, lưu game, điểm, cân bằng game
```

Giao diện **chỉ gọi `GameEngine`**; chỉ `GameEngine` được thay đổi `GameState`. Khi có gì thay đổi, engine báo cho giao diện qua `GameListener`.

## 4. Kiến thức OOP thể hiện trong game (để giải thích khi bảo vệ)

| Kiến thức | Ở đâu |
|---|---|
| Lớp trừu tượng | `Car`, `Customer` (có phương thức `abstract`) |
| Kế thừa | 6 lớp xe kế thừa `Car`, 5 lớp khách kế thừa `Customer` |
| Đa hình | `customer.accepts(car)`, `customer.price(car)`, `car.getWearPerDay()`, `event.apply(...)`: cùng một lời gọi, mỗi lớp làm khác nhau. `SportsCar` ghi đè `getRepairCost()` |
| Interface | `RandomEvent` (7 lớp cài đặt), `GameListener` |
| Đóng gói | tiền trong `GameState` chỉ đổi qua `earn()`, `receive()`, `spend()`, `charge()` |
| Enum có hành vi | `Level.of(doanhThu)`, `Achievement.isReached(state)`, `CarStatus` |
| Factory | `CustomerFactory`, `EventFactory`, `CarCatalog` |
| Observer | `GameEngine` báo cho `MainWindow` qua `GameListener` (đổi số liệu, lên cấp, thành tích) |
| Exception tự định nghĩa | `GameException` ("Không đủ tiền!", "Xe đang hỏng"...) – giao diện hiện thông báo |
| Collections, Stream, lambda | danh sách xe/khách/lượt thuê; `stream().filter(...)` trong `Achievement`, `CustomerFactory`... |
| Đọc ghi file | `SaveManager` (ObjectOutputStream – lưu cả ván), `ScoreBoard` (file văn bản `highscores.txt`) |
| Generics | `ListTableModel<T>` dùng cho mọi bảng |
| Unit test | JUnit 5; `GameEngine` nhận `Random` từ bên ngoài nên test dùng seed cố định, kết quả luôn giống nhau |

## 5. Kịch bản demo 5 phút

1. `run.bat` → nhập tên → **Ván mới**.
2. Chọn một *Sinh viên*, chọn xe *Kia Morning* (cột "Với khách chọn" hiện giá) → **Cho thuê**: tiền tăng ngay, hiện thành tích *Khai trương*.
3. **Ngày tiếp theo** → bảng tổng kết ngày; xe trả về bị **bẩn** → **Rửa xe**.
4. **Cửa hàng xe** → mua *Toyota Innova* → cho khách *Gia đình* thuê. Thấy sự kiện ngẫu nhiên trong bảng tổng kết.
5. Về **Màn hình chính** → **Ván demo** (bắt đầu ngày 25, cấp 4, 5 xe) → chơi vài ngày đến hết ngày 30 → màn hình **xếp hạng** và **bảng xếp hạng**.
6. Mở code: `Car` và các lớp con, `Customer` và các lớp con, các lớp `RandomEvent`, `GameEngine.rent()`.

> Phiên bản cũ "FleetCare" (quản lý đội xe, 37 chức năng) được giữ nguyên ở thư mục `C:\Users\Admin\fleet-care-v1-backup`.
