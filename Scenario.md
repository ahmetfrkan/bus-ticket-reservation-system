# Otobüs Bilet Rezervasyon Sistemi

Bu proje; otobüs koltuk yerleşimini modelleyen, seferleri yöneten, yolculara koltuk rezerve eden ve bilet fiyatı hesaplayan basit bir konsol uygulamasıdır.

Aşağıdaki maddeler yapılacak işleri ve proje bileşenlerini anlatır.

---

## 1. Koltuk Düzeni (BusSimple)

- **Amaç:** Otobüsün fiziksel koltuklarını oluşturmak.
- Otobüs şu bilgileri bilir:
    - Koltuk dizilimi: `2+1` veya `2+2` (`SeatFormat` enum).
    - Toplam koltuk sayısı (örn. 49).
    - Arka bank var mı?
        - `2+1` için: opsiyonel 4'lü bank.
        - `2+2` için: her zaman 5'li bank.
    - Numara stili:
        - `CONTINUOUS`: 1,2,3,4,...
        - `SKIPPING`: firma standardı numaralandırma (atlayarak).
- Otobüs üretirken:
    - Koltuklar `Seat` objeleri olarak oluşturulur.
    - Her `Seat` şunları tutar:
        - `seatNumber` (ör. 17)
        - `rowIndex` (hangi sırada olduğu, 0-based; arka bank için `-1`)
        - `SeatPosition` (soldaki, sağ iç, sağ cam, arka bank vs.)
- Kapı kuralı:
    - Belirli bir sırada (`DOOR_ROW_INDEX`) sağ tarafta koltuk yoktur (orta kapı yüzünden).
    - Diğer sıralarda sağ taraf normal gelir.
- Arka bank:
    - `2+1`: varsa 4 koltuk.
    - `2+2`: her zaman 5 koltuk.
- BusSimple içinde ayrıca basit bir `printSeats()` metodu vardır:
    - Koltukları otobüs gibi yazar.
    - `2+1` görünümü: `[L]    [R R]`
    - `2+2` görünümü: `[L L]    [R R]`
    - Arka bank en son tek satırda basılır.

**Görevler:**
- [ ] `SeatFormat`, `NumberingStyle`, `SeatPosition` enumlarını tanımla.
- [ ] `Seat` sınıfını yaz.
- [ ] `BusSimple` sınıfını yaz:
    - [ ] Koltukları oluştur (`generateSeats`).
    - [ ] Kapı sırası için sağ taraf üretme.
    - [ ] Arka bankı ekle.
    - [ ] `getSeats()` ile tüm koltukları dışarı ver.
    - [ ] `printSeats()` ile düzeni konsola yazdır.

---

## 2. Sefer (Trip)

- **Amaç:** Gerçek bir otobüs seferini temsil etmek.
- Trip şu bilgileri tutar:
    - Nereden (`from`) ve nereye (`to`) gidiyor.
    - Kalkış zamanı (örn. `"2025-10-28 08:00"`).
    - Mesafe (km olarak, örn. `450`).
    - Yolculuk tipi (`TripType` enum):
        - `ONE_WAY`
        - `ROUND_TRIP`
    - Hangi otobüsle gidiliyor? (`BusSimple` referansı).
- Trip ayrıca rezerve edilen koltukları takip eder:
    - `Map<Integer, PassengerInfo> reservations`
    - Key: koltuk numarası.
    - Value: o koltuğu alan yolcunun bilgisi.
- Bir koltuğun rezerve edilmesi:
    - `reserveSeatAndIssueTicket(seatNumber, fullName, age)` metodu ile yapılır.
    - Koltuk müsaitse:
        - Yolcuyu kaydeder.
        - `Ticket` üretir (fiyat dahil).
        - `Ticket` return eder.
    - Koltuk doluysa veya yoksa:
        - `null` döner ve bilgi mesajı gösterilebilir.
- Koltuk durumu çizimi:
    - `printSeatAvailability()` metodu sefer özelinde otobüsü çizer.
    - Her fiziksel koltuk şu formatta gösterilir:
        - `[05 o]` → 5 numaralı koltuk boş (`o`)
        - `[12 x]` → 12 numaralı koltuk dolu (`x`)
    - Kapı olan sırada sağ taraf boş olarak çizilir.
    - Arka bank en son toplu halde gösterilir.

**Görevler:**
- [ ] `TripType` enumunu oluştur (`ONE_WAY`, `ROUND_TRIP`).
- [ ] `PassengerInfo` sınıfını yaz (yolcu adı + yaş).
- [ ] `Trip` sınıfını yaz:
    - [ ] Sefer bilgilerini tut.
    - [ ] `reservations` map'ini yönet.
    - [ ] `isSeatTaken(seatNumber)` metodu.
    - [ ] `reserveSeatAndIssueTicket(...)` metodu.
    - [ ] `printSeatAvailability()` metodu:
        - [ ] Her koltuğu `[num durum]` olarak bas.
        - [ ] Durumu `o` (boş) / `x` (dolu) olarak göster.
        - [ ] Arka bankı en sona yaz.

---

## 3. Yolcu Bilgisi (PassengerInfo)

- **Amaç:** Koltuğu kimin aldığı bilgisini ayrı tutmak.
- İçerik:
    - Yolcu adı (`fullName`)
    - Yaş (`age`)
- Bu sınıfı ayrıca ileride genişletebiliriz:
    - Cinsiyet
    - Telefon
    - TC Kimlik / pasaport
    - vb.

**Görevler:**
- [ ] `PassengerInfo` sınıfını yaz:
    - [ ] Constructor'da basit doğrulama yap (`age > 0`, isim boş olmasın).
    - [ ] Getter'lar ekle.

---

## 4. Bilet (Ticket) ve Fiyatlandırma

- **Amaç:** Bir rezervasyon sonrasında kesilen bilet ve fiyat bilgisini temsil etmek.
- Ticket şunları tutar:
    - Hangi sefer (`Trip`)
    - Hangi koltuk numarası (`seatNumber`)
    - Hangi yolcu (`PassengerInfo`)
    - Toplam ücret (`finalPrice`)
- Fiyat hesaplama kuralları (bizim versiyonumuz):
    1. Mesafeye göre taban ücret:
        - Tek yön (`TripType.ONE_WAY`): `1.0 TL/km`
        - Gidiş-dönüş (`TripType.ROUND_TRIP`): `2.0 TL/km`
            - Yani aslında gidiş ve dönüş birlikte fiyatlanıyor.
    2. Premium koltuk ücreti:
        - Eğer koltuk numarası 3 veya 3’ün katıysa %20 zam (`*1.20`).
        - (Gerekirse bunu ileride "tekli koltuklar pahalıdır" diye değiştirebiliriz.)
    3. İndirimler sırayla uygulanır:
        - Gidiş-dönüş indirimi: son fiyat üzerinden %20 indirim.
        - Yaş indirimi:
            - < 12 yaş: %50 indirim.
            - > 65 yaş: %30 indirim.
- Ticket `toString()` metodu ile bilet özetini yazabilir:
    - Nereden → nereye
    - Tarih / saat
    - Koltuk numarası
    - Yolcu adı + yaş
    - Fiyat

**Görevler:**
- [ ] `Ticket` sınıfını yaz:
    - [ ] Constructor: `new Ticket(trip, seatNumber, passenger)`
    - [ ] `calculatePrice()` metodu:
        - [ ] km * fiyatlandırma
        - [ ] premium koltuk çarpanı
        - [ ] indirimler
    - [ ] `toString()` ile ekrana yazdırılabilir hale getir.

- [ ] `Trip.reserveSeatAndIssueTicket(...)` içinde:
    - [ ] Koltuk boş mu kontrol et.
    - [ ] Yolcuyu `reservations` map'ime yaz.
    - [ ] `Ticket` oluştur ve geri dön.

---

## 5. Konsol Arayüzü (Main)

- **Amaç:** Kullanıcıyla etkileşim kuran uç senaryo.
- Akış:
    1. Sistem belli `Trip` nesnelerini önceden tanımlar.  
       Örnek:
        - Ankara → İstanbul, 2025-10-28 08:00, 450 km, ONE_WAY, 2+1 otobüs
        - Ankara → İstanbul, 2025-10-28 14:30, 450 km, ONE_WAY, 2+2 otobüs
        - Ankara → İzmir,   2025-10-29 07:45, 590 km, ROUND_TRIP, 2+1 otobüs
    2. Kullanıcıdan şunlar istenir:
        - Nereden gitmek istiyorsun?
        - Nereye gitmek istiyorsun?
        - Hangi tarih? (`yyyy-mm-dd`)
    3. Bu bilgiye göre uygun seferler filtrelenir.
        - Yani `from`, `to` ve `departureTime`'ın tarihi eşleşen `Trip`'ler bulunur.
    4. Kullanıcıya sadece bu eşleşen seferler listelenir:
        - Her satırda şunlar yazılır:
            - Kalkış saati
            - Mesafe
            - Yolculuk tipi (ONE_WAY / ROUND_TRIP)
            - Otobüs dizilimi (2+1 mi 2+2 mi)
    5. Kullanıcı bu listeden bir sefer seçer.
    6. Sistem seçilen sefer için koltuk planını gösterir:
        - `[01 o] [02 o] ...`
        - `o = boş`, `x = dolu`
        - `2+1` ile `2+2` farkı görsel olarak barizdir.
    7. Rezervasyon döngüsü başlar:
        - Kullanıcıdan yolcu adı istenir.
            - Çıkış için `-1` yazabilir.
        - Yaş istenir.
        - Koltuk numarası istenir.
        - Sistem:
            - Koltuk uygun mu kontrol eder.
            - Uygunsa rezervasyonu yapar, `Ticket` oluşturur.
            - Bileti ve fiyatını yazdırır.
            - Güncel koltuk planını tekrar çizer (artık `x` gözükür).
        - Bu döngü kullanıcı `-1` diyene kadar sürer.

**Görevler:**
- [ ] `Main` sınıfını yaz:
    - [ ] Birkaç tane `BusSimple` oluştur (ör. 2+1 ve 2+2).
    - [ ] Bu otobüslerle birkaç `Trip` oluşturup `allTrips` listesine koy.
    - [ ] Kullanıcıdan `from`, `to`, `date` al.
    - [ ] Uyan seferleri filtrele ve listele.
        - [ ] Burada ayrıca dizilimi yaz: `Düzen: 2+1` veya `Düzen: 2+2`.
    - [ ] Kullanıcıya "hangi seferi istiyorsun?" diye sor.
    - [ ] Seçilen `Trip` üstünde:
        - [ ] `printSeatAvailability()`
        - [ ] döngüyle yolcu bilgisi al (`-1` ile çıkış)
        - [ ] `reserveSeatAndIssueTicket(...)`
        - [ ] bilet bilgilerini yaz
        - [ ] tekrar `printSeatAvailability()`

---

## 6. Geliştirme için İleri Fikirler (ileriki derslerde eklenebilir)

Bunlar şu an şart değil ama sistemin doğal genişleme noktaları. Sunumda "ileride şunu da yapabiliriz" diye hava atmalık maddeler:

- Koltuk yanına cinsiyet kısıtı (örneğin kadın yanına rastgele erkek oturtma engeli).
- 12 yaş altı yalnız seyahat yasağı.
- Ön sıralar daha pahalı olsun (dinamik fiyat).
- Satın alınan biletlerin listesi bir dosyaya yazılsın (dosya I/O konusu).
- Bir koltuk iptal edilsin (rezervasyonu silme).
- Aynı gün içinde birden fazla seferi gezip bilet almaya devam et (mini menü loop).

---

## 7. Proje Bittiğinde Öğrencinin Öğrenmiş Olacağı Şeyler

Bu proje bittikten sonra öğrenci aslında şunları kafasına yazmış olacak:

- `class` nasıl yazılır (state + behavior).
- `enum` ile sabit anlamlar nasıl temsil edilir.
- Nesneler birbirini nasıl kullanır (Trip -> BusSimple, Ticket -> Trip).
- List ve Map kullanımı.
- Konsolda kullanıcıdan veri alma (`Scanner`) ve string parse etme.
- "Durum" göstermek (`o` / `x`) ve onu güncellemek (state change).
- Fiyat hesaplama iş mantığını sınıfa koymak (Ticket).

Yani bu proje, "sadece if-else yazıyoruz" seviyesinden çıkıp, "ufak bir sistem yazıyoruz" seviyesine sokuyor. Yani tam hedef bu.

---

## 8. Minimum Çalışır Hal (MVP checklist)

Şu kutular tiklenince proje ayağa kalkar:

- [ ] `SeatFormat`, `NumberingStyle`, `TripType`, `SeatPosition` enumları yazıldı.
- [ ] `Seat` yazıldı.
- [ ] `PassengerInfo` yazıldı.
- [ ] `BusSimple` yazıldı ve `getSeats()` + `printSeats()` çalışıyor.
- [ ] `Trip` yazıldı, rezervasyon map'i var, `printSeatAvailability()` var.
- [ ] `Ticket` yazıldı, fiyat kurallarıyla birlikte.
- [ ] `Main` yazıldı:
    - [ ] Sefer filtreleme by (from, to, date)
    - [ ] Sefer seçme
    - [ ] Koltuk göstermek
    - [ ] Yolcu eklemek ve bilet kesmek döngüsü (-1 çıkış)

Bu kadar.