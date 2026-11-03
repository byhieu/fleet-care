package com.cartycoon.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CarTest {

    @Test
    @DisplayName("Sửa xe: (100 - tình trạng)% x giá mua x 0,3")
    void repairCost() {
        Car vios = new Sedan("51A-1", "Toyota Vios").used(60, 0, true);
        assertEquals(840_000, vios.getRepairCost()); // 40% x 7.000.000 x 0,3
    }

    @Test
    @DisplayName("Xe thể thao: sửa đắt hơn 50% (lớp con ghi đè phương thức)")
    void sportsCarRepairIsMoreExpensive() {
        Car mx5 = new SportsCar("51A-2", "Mazda MX-5").used(60, 0, true);
        assertEquals(4_500_000, mx5.getRepairCost()); // 40% x 25.000.000 x 0,3 x 1,5
    }

    @Test
    @DisplayName("Bán xe: giá mua x tình trạng% x 0,6")
    void sellPrice() {
        Car vios = new Sedan("51A-1", "Toyota Vios").used(60, 0, true);
        assertEquals(2_520_000, vios.getSellPrice());
    }

    @Test
    @DisplayName("Tình trạng dưới 30% thì xe hỏng, sửa xong thì rảnh lại")
    void brokenAndRepaired() {
        Car car = new CityCar("51A-3", "Kia Morning").used(35, 0, true);
        car.wear(10);
        assertEquals(CarStatus.BROKEN, car.getStatus());
        car.repair();
        assertEquals(100, car.getCondition());
        assertEquals(CarStatus.FREE, car.getStatus());
    }

    @Test
    @DisplayName("Khách trả xe: xe luôn bẩn, hỏng nếu bị chạy quá sức")
    void giveBack() {
        Car car = new Suv("51A-4", "Toyota Innova");
        car.rentOut();
        car.wear(75);
        assertEquals(CarStatus.RENTED, car.getStatus()); // still on the road
        car.giveBack();
        assertFalse(car.isClean());
        assertEquals(CarStatus.BROKEN, car.getStatus());
    }

    @Test
    @DisplayName("Mỗi loại xe có số chỗ và giá riêng (đa hình)")
    void eachTypeHasItsOwnValues() {
        assertEquals(4, new CityCar("a", "x").getSeats());
        assertEquals(7, new Suv("a", "x").getSeats());
        assertEquals(16, new Van("a", "x").getSeats());
        assertEquals(4_500_000, new LuxuryCar("a", "x").getDailyPrice());
    }
}
