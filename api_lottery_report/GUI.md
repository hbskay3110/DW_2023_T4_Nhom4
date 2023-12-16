### API – Spring boot – MySQL
#### 1. Các thành phần của hệ thống bao gồm:
-	Spring boot phiên bản 2.7.17
-	ORM: Spring data JPA
-	MySQL phiên bản 8.0
-	Java JDK 17


#### 2. Có 4 API được cung cấp cho trang web
GET: http://localhost:8080/api/location/code: Chứa các mã code các tỉnh được dùng trên param đường dẫn.
Ví dụ: Miền Bắc có code là “mien_bac”, Quảng Ngãi có code là “quang_ngai”…..


 
GET: http://localhost:8080/api/regions: Chức tất cả các miền và trong miền thì có nhiều đài: 
   

 
GET: http://localhost:8080/api/lottery/{location}: Chứa kết quả từng đài cụ thể ngày mới nhất.Trong path variable /{location} là code của đài đó.
Ví dụ: http://localhost:8080/api/lottery/quang_ngai

 
 
GET: http://localhost:8080/api/lottery/{location}/{date}: Chứa kết quả từng đài cụ thể với ngày cụ thể.Trong đó path variable có 2 thông số:
-	/{location} là code của đài đó ví dụ “quang_ngai”.
-	/{date} là ngày chi tiết cụ thể, có dạng dd-mm-yyyy , ví dụ 02-12-2023
Ví dụ: http://localhost:8080/api/lottery/quang_ngai/2-12-2023


