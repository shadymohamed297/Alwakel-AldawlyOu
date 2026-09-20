# تطبيق مركز الصيانة المتكامل

_Started 2026-09-13 09:23 UTC_

---

## User

Design a mobile app for سيستم مركز صيانة متكامل

## Assistant



## User

Answered — domain: صيانة أجهزة منزلية (تكييف، غسالات); user: الفني / الميداني (ينفّذ أوامر العمل), موظف الاستقبال / خدمة العملاء, المدير (لوحة أداء وتقارير); lang: عربي RTL فقط; platform: Android; scope: 8

## Assistant



## User

Answered — density: متوازن

## Assistant

_[tool: copy_starter_component]_

_[tool: read_file]_

_[tool: read_file]_

_[tool: dc_write]_

_[tool: ready_for_verification]_

ثمانية شاشات أندرويد بعربي RTL، مقسّمة على ثلاثة أدوار: الاستقبال (لوحة + استلام طلب + تعيين فني)، الفني (مهام اليوم + تنفيذ أمر العمل + الفاتورة والتوقيع)، المدير (لوحة الأداء + المخزون).

الهوية: أخضر بترولي أساسي، برتقالي للعاجل، خط IBM Plex Sans Arabic، أرقام عربية.

لو تحب أضيف شاشات لتقارير الفنيين أو بوابة العميل، قل لي.

## User

هو مركز الصيانة فيي مصر القاهرة مش الرياض ماشي متخصص في صيانة جميع الاجهزة المنزلية سخان بوتاجاز تكيف غسالة الي اخره الابلكيشن فهمت كده ؟

## Assistant

_[tool: run_script]_

_[tool: str_replace_edit]_

_[tool: ready_for_verification]_

فهمت. حدّثت التطبيق: القاهرة (مدينة نصر، المعادي، الرحاب)، أسماء وأرقام مصرية، العملة ج.م وضريبة ١٤٪، وأنواع الأجهزة صارت مكيف/غسالة/ثلاجة/سخان/بوتاجاز — وتوزيع الأعطال وقطع الغيار في لوحة المدير يعكس كل الأجهزة (عنصر تسخين سخان بدل مضخة الغسالة).

## User

Found issues — fixing…

## Assistant

_[tool: str_replace_edit]_

_[tool: ready_for_verification]_

عدّلت شاشة المدير: دمجت البوتاجاز والثلاجات في صف واحد (٢٦٪) وقصّرت رسم الإيراد الأسبوعي — كل البطاقات صارت داخل حدود الشاشة.

## User

Save this design as a PDF: `مركز الصيانة.dc.html`

## Assistant

_[tool: read_file]_

_[tool: copy_starter_component]_

_[tool: copy_files]_

_[tool: str_replace_edit]_

_[tool: show_html]_

_[tool: show_html]_

_[tool: show_pdf_export_dialog]_

جهّزت نسخة الطباعة: ٣ صفحات أفقية (Letter)، صفحة لكل دور بشاشاتها الثلاث. نافذة التصدير ظاهرة — اضغط «Print or save as PDF» لإتمام الحفظ.

