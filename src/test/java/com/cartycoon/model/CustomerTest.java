package com.cartycoon.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {
    private final Car morning = new CityCar("a", "Kia Morning");
    private final Car vios = new Sedan("b", "Toyota Vios");
    private final Car innova = new Suv("c", "Toyota Innova");
    private final Car transit = new Van("d", "Ford Transit");
    private final Car mx5 = new SportsCar("e", "Mazda MX-5");
    private final Car mercedes = new LuxuryCar("f", "Mercedes S450");

    @Test
    @DisplayName("Sinh viên: chỉ nhận xe tối đa 900.000 đ/ngày, trả 80%")
    void student() {
        Customer s = new StudentCustomer("Em Vy", 2);
        assertTrue(s.accepts(vios));
        assertFalse(s.accepts(innova));
        assertEquals(1_280_000, s.price(vios)); // 800.000 x 2 ngày x 0,8
    }

    @Test
    @DisplayName("Sinh viên không chê xe bẩn, gia đình thì có")
    void dirtyCar() {
        vios.makeDirty();
        assertTrue(new StudentCustomer("a", 1).isHappyWith(vios));
        innova.makeDirty();
        assertFalse(new FamilyCustomer("b", 1).isHappyWith(innova));
    }

    @Test
    @DisplayName("Gia đình cần 7 chỗ, đoàn du lịch cần 16 chỗ")
    void seats() {
        assertFalse(new FamilyCustomer("a", 1).accepts(vios));
        assertTrue(new FamilyCustomer("a", 1).accepts(innova));
        assertFalse(new TouristCustomer("b", 2).accepts(innova));
        assertTrue(new TouristCustomer("b", 2).accepts(transit));
    }

    @Test
    @DisplayName("Dân chơi chỉ nhận xe thể thao, trả x1,5 và làm xe hao mòn gấp đôi")
    void racer() {
        Customer r = new RacerCustomer("a", 1);
        assertFalse(r.accepts(mercedes));
        assertTrue(r.accepts(mx5));
        assertEquals(4_500_000, r.price(mx5));
        assertEquals(2, r.getWearMultiplier());
    }

    @Test
    @DisplayName("Sếp lớn: sedan hoặc xe sang, phải sạch và từ 80%, bo 10%")
    void boss() {
        Customer b = new BossCustomer("a", 1);
        assertTrue(b.accepts(mercedes));
        assertFalse(b.accepts(morning));
        assertFalse(b.accepts(new Sedan("x", "Vios").used(70, 0, true)));
        assertFalse(b.accepts(new Sedan("y", "Vios").used(90, 0, false)));
        assertEquals(500_000, b.getTip(5_000_000));
    }
}
