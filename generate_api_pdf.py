from fpdf import FPDF, XPos, YPos
import os, shutil

LOGO_SRC  = r"D:\Nalar\app\src\main\res\drawable\logo_nalar_premium.png"
LOGO_TMP  = r"D:\Nalar\deliverables\_logo_tmp_api.png"
OUTPUT    = r"D:\Nalar\deliverables\2_Source_Code_API_Documentation.pdf"
shutil.copy2(LOGO_SRC, LOGO_TMP)

# Palette
NAVY   = (15, 40, 80)
BLUE   = (26, 82, 160)
CYAN   = (0, 164, 204)
LBLUE  = (235, 245, 255)
WHITE  = (255, 255, 255)
DARK   = (30, 50, 80)
BODY   = (55, 65, 85)
GREY   = (100, 110, 130)
LGREY  = (230, 235, 242)
CODEBG = (248, 250, 253)


class Doc(FPDF):
    def header(self):
        if self.page_no() == 1:
            return
        self.set_fill_color(*BLUE)
        self.rect(0, 0, 210, 1.5, 'F')
        self.set_font("Helvetica", "B", 8)
        self.set_text_color(*BLUE)
        self.set_xy(14, 3)
        self.cell(0, 5, "NALAR  |  Dokumentasi Source Code API Backend  |  XI PPLG 3")
        self.set_draw_color(*LGREY)
        self.set_line_width(0.3)
        self.line(14, 10, 196, 10)
        self.set_y(15)

    def footer(self):
        if self.page_no() == 1:
            return
        self.set_draw_color(*LGREY)
        self.set_line_width(0.3)
        self.line(14, 284, 196, 284)
        self.set_font("Helvetica", "", 8)
        self.set_text_color(*GREY)
        self.set_xy(14, 285)
        self.cell(0, 5, "Kelompok Nalar  *  XI PPLG 3")
        self.set_xy(14, 285)
        self.cell(0, 5, "Halaman " + str(self.page_no() - 1), align="R")


def cover(pdf):
    pdf.add_page()
    pdf.set_auto_page_break(False)

    # Top bar
    pdf.set_fill_color(*BLUE)
    pdf.rect(0, 0, 210, 20, 'F')
    pdf.set_fill_color(*CYAN)
    pdf.rect(0, 20, 210, 3, 'F')
    pdf.set_font("Helvetica", "B", 9)
    pdf.set_text_color(*WHITE)
    pdf.set_xy(0, 6)
    pdf.cell(210, 8, "SMK NEGERI  |  KELOMPOK NALAR  |  XI PPLG 3", align="C")

    # Logo in white circle
    pdf.set_fill_color(*WHITE)
    pdf.ellipse(82, 32, 46, 46, 'F')
    pdf.image(LOGO_TMP, x=84, y=34, w=42)

    # Title
    pdf.set_font("Helvetica", "B", 21)
    pdf.set_text_color(*DARK)
    pdf.set_xy(0, 86)
    pdf.cell(210, 11, "DOKUMENTASI SOURCE CODE", align="C",
             new_x=XPos.LMARGIN, new_y=YPos.NEXT)
    pdf.cell(210, 11, "API BACKEND EXPRESS", align="C",
             new_x=XPos.LMARGIN, new_y=YPos.NEXT)
    
    # Accent line
    pdf.set_draw_color(*CYAN)
    pdf.set_line_width(1.2)
    pdf.line(68, 110, 142, 110)
    
    # Subtitle
    pdf.set_font("Helvetica", "", 11)
    pdf.set_text_color(*GREY)
    pdf.set_xy(0, 114)
    pdf.cell(210, 7, "Aplikasi Nalar", align="C",
             new_x=XPos.LMARGIN, new_y=YPos.NEXT)
    pdf.set_font("Helvetica", "I", 9)
    pdf.cell(210, 6, "Premium Gamified Full-Stack Learning Platform", align="C",
             new_x=XPos.LMARGIN, new_y=YPos.NEXT)

    # Info box
    bx, by, bw, bh = 35, 133, 140, 55
    pdf.set_fill_color(*LBLUE)
    pdf.rect(bx, by, bw, bh, 'F')
    pdf.set_draw_color(*LGREY)
    pdf.set_line_width(0.3)
    pdf.rect(bx, by, bw, bh)
    pdf.set_fill_color(*BLUE)
    pdf.rect(bx, by, 3, bh, 'F')
    rows = [("Nama Aplikasi","Nalar"), ("Platform","Backend RESTful API (Express, MySQL)"),
            ("Kelompok","Nalar"), ("Kelas","XI PPLG 3")]
    for i,(lbl,val) in enumerate(rows):
        y0 = by + 7 + i*12
        pdf.set_font("Helvetica","",9); pdf.set_text_color(*GREY)
        pdf.set_xy(bx+7, y0); pdf.cell(45,6,lbl)
        pdf.set_font("Helvetica","B",9); pdf.set_text_color(*DARK)
        pdf.cell(0, 6, val)

    # Members heading
    pdf.set_font("Helvetica","B",9); pdf.set_text_color(*BLUE)
    pdf.set_xy(0,197); pdf.cell(210,6,"ANGGOTA KELOMPOK",align="C",
             new_x=XPos.LMARGIN,new_y=YPos.NEXT)
    pdf.set_draw_color(*LGREY); pdf.set_line_width(0.3)
    pdf.line(50,205,160,205)

    members = [
        ("Davinza Fattah Dzulhijriyan Syahid","541241043","Ketua & Full-Stack Developer"),
        ("Jonathan Bima Pradana Putra","541241097","UI/UX Designer"),
        ("Khafidz Asad Hermawan","541241105","UI/UX Designer"),
        ("Muhammad Ridlo Al Qohhaar","541241141","UI/UX Designer & Poster"),
        ("Verel Chandra Riyanto","541241195","UI/UX Designer"),
    ]
    pdf.set_font("Helvetica","",8.5); pdf.set_text_color(*DARK)
    y0 = 209
    for name, nis, role in members:
        pdf.set_xy(20, y0)
        pdf.set_font("Helvetica","B",8.5); pdf.write(6, name+"  ")
        pdf.set_font("Helvetica","",8.5); pdf.set_text_color(*GREY)
        pdf.write(6,"NIS "+nis+"   *   "+role)
        pdf.set_text_color(*DARK)
        y0 += 9

    # Bottom bar
    pdf.set_fill_color(*NAVY)
    pdf.rect(0, 266, 210, 31, 'F')
    pdf.set_fill_color(*CYAN)
    pdf.rect(0, 266, 210, 2, 'F')
    pdf.set_font("Helvetica","",8); pdf.set_text_color(*WHITE)
    pdf.set_xy(0, 278)
    pdf.cell(210, 5, "github.com/Dialius/Nalar  |  2026  |  Dokumen Akademis", align="C")


# ── Helpers ───────────────────────────────────────────────────────────────────
L, R, TW = 14, 14, 182

def sec(pdf, n, title):
    if pdf.get_y() > 215:
        pdf.add_page()
    pdf.ln(4)
    y = pdf.get_y()
    pdf.set_fill_color(*BLUE); pdf.rect(L, y, 3.5, 10, 'F')
    pdf.set_font("Helvetica","B",13); pdf.set_text_color(*DARK)
    pdf.set_xy(L+6, y); pdf.cell(0, 10, str(n)+". "+title,
                                  new_x=XPos.LMARGIN, new_y=YPos.NEXT)
    pdf.ln(2)

def sub(pdf, txt):
    if pdf.get_y() > 235:
        pdf.add_page()
    pdf.set_font("Helvetica","B",10); pdf.set_text_color(*DARK)
    pdf.set_x(L); pdf.cell(0,7,txt,new_x=XPos.LMARGIN,new_y=YPos.NEXT)
    pdf.ln(1)

def para(pdf, txt):
    pdf.set_font("Helvetica","",10); pdf.set_text_color(*BODY)
    pdf.set_x(L); pdf.multi_cell(TW, 6, txt)
    pdf.ln(2)

def bul(pdf, bold, rest):
    if pdf.get_y() > 265:
        pdf.add_page()
    y = pdf.get_y()
    pdf.set_fill_color(*BLUE); pdf.rect(L+2, y+3.2, 1.8, 1.8, 'F')
    pdf.set_left_margin(L+9)
    pdf.set_xy(L+9, y)
    pdf.set_font("Helvetica","B",10); pdf.set_text_color(*DARK)
    pdf.write(6, bold)
    pdf.set_font("Helvetica","",10); pdf.set_text_color(*BODY)
    pdf.write(6, rest)
    pdf.ln(8)
    pdf.set_left_margin(L)

def step(pdf, n, txt):
    if pdf.get_y() > 260:
        pdf.add_page()
    y = pdf.get_y()
    pdf.set_fill_color(*BLUE); pdf.rect(L, y, 7, 7, 'F')
    pdf.set_font("Helvetica","B",8); pdf.set_text_color(*WHITE)
    pdf.set_xy(L, y+0.5); pdf.cell(7, 6, str(n), align="C")
    pdf.set_left_margin(L+10)
    pdf.set_xy(L+10, y)
    pdf.set_font("Helvetica","",10); pdf.set_text_color(*BODY)
    pdf.write(6, txt)
    pdf.ln(9)
    pdf.set_left_margin(L)

def code(pdf, lines, title=None):
    lh = 5.2
    th = 8 if title else 0
    bh = th + len(lines)*lh + 8
    if pdf.get_y() + bh > 277:
        pdf.add_page()
    x, y = L, pdf.get_y()
    pdf.set_fill_color(*CODEBG); pdf.rect(x, y, TW, bh, 'F')
    pdf.set_draw_color(210, 225, 245); pdf.set_line_width(0.2)
    pdf.rect(x, y, TW, bh)
    pdf.set_fill_color(*CYAN); pdf.rect(x, y, 3, bh, 'F')
    if title:
        pdf.set_fill_color(*LBLUE); pdf.rect(x+3, y, TW-3, th, 'F')
        pdf.set_font("Helvetica","B",7.5); pdf.set_text_color(*BLUE)
        pdf.set_xy(x+7, y+1.5); pdf.cell(170, 5, title)
    pdf.set_y(y + th + 4)
    pdf.set_font("Courier","",8.5); pdf.set_text_color(*DARK)
    for ln in lines:
        pdf.set_x(x+6); pdf.cell(TW-8, lh, ln,
                                   new_x=XPos.LMARGIN, new_y=YPos.NEXT)
    pdf.ln(5)

def hrule(pdf):
    pdf.set_draw_color(*LGREY); pdf.set_line_width(0.3)
    pdf.line(L, pdf.get_y(), 210-R, pdf.get_y()); pdf.ln(5)

def callout(pdf, txt):
    if pdf.get_y() > 255:
        pdf.add_page()
    y = pdf.get_y()
    pdf.set_fill_color(*LBLUE); pdf.rect(L, y, TW, 16, 'F')
    pdf.set_fill_color(*CYAN);  pdf.rect(L, y, 3, 16, 'F')
    pdf.set_font("Helvetica","B",8); pdf.set_text_color(*CYAN)
    pdf.set_xy(L+6, y+3); pdf.write(5,"CATATAN KEAMANAN:  ")
    pdf.set_font("Helvetica","",9); pdf.set_text_color(*BODY)
    pdf.write(5, txt); pdf.ln(10)


# ── BUILD ─────────────────────────────────────────────────────────────────────
pdf = Doc(orientation="P", unit="mm", format="A4")
pdf.set_margins(L, 14, R)

cover(pdf)
pdf.set_auto_page_break(True, margin=18)

# ─── PAGE 2 ──────────────────────────────────────────────────────────────────
pdf.add_page()

sec(pdf, 1, "Arsitektur Sistem Backend API")
para(pdf,
    "Backend API Nalar dirancang menggunakan pola RESTful API Architecture "
    "yang modular, asinkron, dan stateless berbasis token JWT (JSON Web Token) "
    "serta MySQL sebagai penyimpanan basis data relasional.")

code(pdf, title="Diagram Alur Request-Response API", lines=[
    "  +-------------------------------------------------------+",
    "  |               CLIENT: Android App                     |",
    "  +-------------------------------------------------------+",
    "                              | REST Requests (Bearer JWT)",
    "                              v",
    "  +-------------------------------------------------------+",
    "  |         EXPRESS ROUTER: routes/                       |",
    "  +-------------------------------------------------------+",
    "                              |",
    "                              v",
    "  +-------------------------------------------------------+",
    "  |         MIDDLEWARE: requireAuth / requireAdmin        |",
    "  +-------------------------------------------------------+",
    "                              | validated request",
    "                              v",
    "  +-------------------------------------------------------+",
    "  |         ROUTE HANDLERS: Business Logic Functions      |",
    "  +---------------+-----------------------+---------------+",
    "                  |                       |",
    "                  v DB queries            v API Calls",
    "          +---------------+       +---------------+",
    "          |  MySQL DB Pool|       | Midtrans Snap |",
    "          |  (mysql2)     |       | Payment API   |",
    "          +---------------+       +---------------+",
])

sub(pdf, "Penjelasan Lapisan Arsitektur:")
bul(pdf, "Express Router Layer: ", "Mengelola perutean rute HTTP secara dinamis ke file modular di folder routes/ (auth, users, courses, gamification, payment).")
bul(pdf, "Middleware Layer (Security): ", "requireAuth mengekstrak dan memverifikasi token JWT dari header Authorization. requireAdmin membatasi rute khusus kelayakan admin.")
bul(pdf, "Business Logic Layer: ", "Controller memproses data kuis, statistik gamification (streak, keys, XP), dan melakukan request token snap Midtrans.")
bul(pdf, "Database Layer: ", "Menggunakan pooling koneksi mysql2 dengan Promise wrapper untuk eksekusi kueri non-blocking.")

hrule(pdf)

sec(pdf, 2, "Struktur Direktori Kode Sumber (/nalar-backend)")
para(pdf, "Pemetaan modul penting di dalam repositori API backend Nalar:")

code(pdf, title="nalar-backend/", lines=[
    "+-- database/",
    "|   +-- db.js            # Inisialisasi Driver Pool MySQL",
    "|",
    "+-- middleware/",
    "|   +-- admin.js         # Penapis Rute Khusus Admin (requireAdmin)",
    "|   +-- auth.js          # Validator Token Sesi JWT (requireAuth)",
    "|",
    "+-- routes/",
    "|   +-- auth.js          # Rute Registrasi & Login (Sign In / Sign Up)",
    "|   +-- courses.js       # Rute Silabus & Progress Level",
    "|   +-- gamification.js  # Rute Perhitungan Streak & Leaderboard",
    "|   +-- payment.js       # Rute Midtrans Snap & Webhook Callback",
    "|   +-- users.js         # Profil & Detail Akun Siswa",
    "|",
    "+-- .env.example         # Templat Variabel Lingkungan Lokal",
    "+-- Nalar_API.json       # Kumpulan Endpoint Uji Postman Client",
    "+-- server.js            # Entrypoint Utama Server Node.js",
])

# ─── PAGE 3 ──────────────────────────────────────────────────────────────────
pdf.add_page()

sec(pdf, 3, "Pustaka Pihak Ketiga & Teknologi Utama")
para(pdf, "Dependensi utama yang menyokong performa dan keamanan REST API Nalar:")

libs = [
    ("Express.js (v5.2.1): ","Server framework Node.js berkinerja tinggi untuk memproses request HTTP."),
    ("mysql2: ","Driver database MySQL yang cepat dengan dukungan pooling koneksi asinkron."),
    ("jsonwebtoken: ","Implementasi token JWT untuk otentikasi sesi stateless klien Android."),
    ("bcryptjs: ","Hashing satu arah yang aman dengan salt 10-rounds untuk menyimpan password."),
    ("midtrans-client: ","SDK resmi Midtrans untuk memanggil SNAP API dan memperoleh token pembayaran."),
    ("nodemon: ","Membantu proses pengembangan lokal dengan memuat ulang server secara dinamis."),
]
for b, r in libs:
    bul(pdf, b, r)

hrule(pdf)

sec(pdf, 4, "Panduan Kompilasi & Menjalankan Source Code")

sub(pdf, "Prasyarat Sistem:")
bul(pdf, "Node.js: ", "Node.js v18 (LTS) atau versi terbaru wajib terpasang.")
bul(pdf, "MySQL Server: ", "Instalasi MySQL lokal untuk menyimpan data pengguna.")
bul(pdf, "npm: ", "Node Package Manager bawaan Node.js.")
pdf.ln(2)

sub(pdf, "Langkah-Langkah Menjalankan Server API:")
steps = [
    "Ekstrak file Source_Code_API.zip ke folder pilihan di komputer Anda.",
    "Buka Terminal, ketik 'npm install' untuk mengunduh semua pustaka Node.js.",
    "Buat database MySQL baru bernama 'nalar_db' dan jalankan skema tabel.",
    "Salin berkas '.env.example' menjadi '.env' lalu isi kredensial MySQL lokal Anda.",
    "Jalankan server pengembangan dengan mengetik 'npm run dev'.",
    "Server akan aktif di port 3000 dan siap menerima request dari Android client.",
]
for i, s in enumerate(steps, 1):
    step(pdf, i, s)

hrule(pdf)

sec(pdf, 5, "Integrasi Finansial & Keamanan Webhook Midtrans")
para(pdf,
    "Sistem pembayaran terintegrasi secara asinkron dengan Midtrans Sandbox Webhook. "
    "Setiap notifikasi status pembayaran dikirimkan langsung oleh Midtrans ke endpoint "
    "'/api/payment/notification' menggunakan metode POST.")
para(pdf,
    "Backend API memverifikasi keaslian pengirim menggunakan Signature Key SHA-512 "
    "yang dicocokkan dengan server key Midtrans. Apabila transaksi bernilai 'settlement' "
    "dan valid, database MySQL segera memperbarui flag 'is_premium = 1' untuk pengguna "
    "bersangkutan guna mengaktifkan hak akses VIP premium.")
callout(pdf,
    "Kredensial database MySQL dan Server Key Midtrans disimpan dengan aman di file "
    ".env dan dikecualikan dari Git (.gitignore) untuk menjamin perlindungan credentials.")

# ── Save ─────────────────────────────────────────────────────────────────────
pdf.output(OUTPUT)
if os.path.exists(LOGO_TMP):
    os.remove(LOGO_TMP)
kb = round(os.path.getsize(OUTPUT)/1024, 1)
print(f"Selesai! {OUTPUT} ({kb} KB)")
