package com.watermelon.model.response;

public enum ELocation {
	// Miền Trung
	MIEN_TRUNG("mien_trung", "Miền Trung"), PHU_YEN("phu_yen", "Phú Yên"),
	THUA_THIEN_HUE("thua_thien_hue", "Thừa Thiên Huế"), DAK_LAK("dak_lak", "Đắk Lắk"),
	QUANG_NAM("quang_nam", "Quảng Nam"), DA_NANG("da_nang", "Đà Nẵng"), KHANH_HOA("khanh_hoa", "Khánh Hòa"),
	QUANG_BINH("quang_binh", "Quảng Bình"), BINH_DINH("binh_dinh", "Bình Định"), QUANG_TRI("quang_tri", "Quảng Trị"),
	GIA_LAI("gia_lai", "Gia Lai"), NINH_THUAN("ninh_thuan", "Ninh Thuận"), QUANG_NGAI("quang_ngai", "Quảng Ngãi"),
	DAK_NONG("dak_nong", "Đắk Nông"), KON_TUM("kon_tum", "Kon Tum"),

	// Miền Nam
	MIEN_NAM("mien_nam", "Miền Nam"), HO_CHI_MINH("ho_chi_minh", "Hồ Chí Minh"), DONG_THAP("dong_thap", "Đồng Tháp"),
	CA_MAU("ca_mau", "Cà Mau"), BEN_TRE("ben_tre", "Bến Tre"), VUNG_TAU("vung_tau", "Vũng Tàu"),
	BAC_LIEU("bac_lieu", "Bạc Liêu"), DONG_NAI("dong_nai", "Đồng Nai"), CAN_THO("can_tho", "Cần Thơ"),
	SOC_TRANG("soc_trang", "Sóc Trăng"), TAY_NINH("tay_ninh", "Tây Ninh"), AN_GIANG("an_giang", "An Giang"),
	BINH_THUAN("binh_thuan", "Bình Thuận"), VINH_LONG("vinh_long", "Vĩnh Long"), BINH_DUONG("binh_duong", "Bình Dương"),
	TRA_VINH("tra_vinh", "Trà Vinh"), LONG_AN("long_an", "Long An"), BINH_PHUOC("binh_phuoc", "Bình Phước"),
	HAU_GIANG("hau_giang", "Hậu Giang"), TIEN_GIANG("tien_giang", "Tiền Giang"), KIEN_GIANG("kien_giang", "Kiên Giang"),
	DA_LAT("da_lat", "Đà Lạt"),

	// Miền Bắc
	MIEN_BAC("mien_bac", "Miền Bắc");

	private final String code;
	private final String name;

	ELocation(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

}
