package com.dio0550.tech_quiz.data

import com.dio0550.tech_quiz.R

/**
 * 「③集中 / ミニマル」デザインの収録データ。
 * ホームのカテゴリ一覧と、ネットワーク分野のサンプル設問（午前レベル）。
 */
object QuizData {

    /** ホーム画面に並べるカテゴリ（デザイン同様の6件）。 */
    val HOME_CATEGORIES = listOf(
        Category("base", "基礎理論", R.drawable.ic_cat_functions, progress = 80, acc = 78),
        Category("net", "ネットワーク", R.drawable.ic_cat_lan, progress = 88, acc = 81),
        Category("sec", "セキュリティ", R.drawable.ic_cat_shield, progress = 44, acc = 58),
        Category("db", "データベース", R.drawable.ic_cat_database, progress = 85, acc = 84),
        Category("algo", "アルゴリズムとプログラミング", R.drawable.ic_cat_code, progress = 63, acc = 71),
        Category("pm", "プロジェクトマネジメント", R.drawable.ic_cat_event_note, progress = 75, acc = 75),
    )

    /** 結果サマリーの分野別正答率（デザイン同様）。 */
    val SUMMARY_BREAKDOWN = listOf(
        Breakdown("プロトコル", 100),
        Breakdown("ルーティング", 83),
        Breakdown("TCP/UDP", 75),
        Breakdown("無線・物理層", 60),
    )

    private fun opts(a: String, i: String, u: String, e: String) =
        listOf(Option("ア", a), Option("イ", i), Option("ウ", u), Option("エ", e))

    /** ネットワーク分野の設問。 */
    val NETWORK_QUESTIONS: List<Question> = listOf(
        Question(
            id = "net-arp",
            category = "ネットワーク",
            categoryIcon = R.drawable.ic_cat_lan,
            text = "TCP/IPネットワークにおいて、IPアドレスから対応するMACアドレスを取得するために使用されるプロトコルはどれか。",
            options = opts("ARP", "RARP", "ICMP", "DHCP"),
            correct = 0,
            explanationBody = "ARP（Address Resolution Protocol）は、宛先の{kw}IPアドレスから対応するMACアドレス{/kw}を問い合わせるプロトコルである。ブロードキャストで「このIPの持ち主はMACアドレスを教えて」と要求し、該当ホストがユニキャストで応答する。",
            points = listOf(
                Point("RARP", "MACアドレスからIPアドレスを求める逆の手順。"),
                Point("ICMP", "pingなど到達性確認やエラー通知に使う。"),
                Point("DHCP", "ホストへIPアドレス等を自動割当てする。"),
            ),
        ),
        Question(
            id = "net-osi3",
            category = "ネットワーク",
            categoryIcon = R.drawable.ic_cat_lan,
            text = "OSI基本参照モデルの第3層（ネットワーク層）に位置するプロトコルはどれか。",
            options = opts("IP", "TCP", "HTTP", "Ethernet"),
            correct = 0,
            explanationBody = "ネットワーク層は経路選択（ルーティング）と論理アドレスによる端末識別を担う層であり、{kw}IP{/kw}が該当する。",
            points = listOf(
                Point("TCP", "第4層（トランスポート層）のプロトコル。"),
                Point("HTTP", "第7層（アプリケーション層）のプロトコル。"),
                Point("Ethernet", "第2層／第1層（データリンク・物理層）の規格。"),
            ),
        ),
        Question(
            id = "net-subnet",
            category = "ネットワーク",
            categoryIcon = R.drawable.ic_cat_lan,
            text = "ネットワーク 192.168.1.0/24 を四つのサブネットに均等分割するとき、適切なサブネットマスクはどれか。",
            options = opts("255.255.255.192", "255.255.255.128", "255.255.255.224", "255.255.255.240"),
            correct = 0,
            explanationBody = "四分割するにはホスト部から2ビット借りて /26 とする。マスクは {kw}255.255.255.192{/kw} となる。",
            points = listOf(
                Point("/25", "2分割（255.255.255.128）。"),
                Point("/27", "8分割（255.255.255.224）。"),
                Point("/28", "16分割（255.255.255.240）。"),
            ),
        ),
        Question(
            id = "net-https-port",
            category = "ネットワーク",
            categoryIcon = R.drawable.ic_cat_lan,
            text = "HTTPSで標準的に使用されるウェルノウンポート番号はどれか。",
            options = opts("443", "80", "22", "25"),
            correct = 0,
            explanationBody = "HTTPSはTLSで暗号化したHTTP通信であり、{kw}443/TCP{/kw}を用いる。",
            points = listOf(
                Point("80", "HTTP（平文）が使用するポート。"),
                Point("22", "SSHが使用するポート。"),
                Point("25", "SMTP（メール送信）が使用するポート。"),
            ),
        ),
        Question(
            id = "net-dns",
            category = "ネットワーク",
            categoryIcon = R.drawable.ic_cat_lan,
            text = "ドメイン名からIPアドレスを求める名前解決を行うプロトコルはどれか。",
            options = opts("DNS", "DHCP", "NTP", "SNMP"),
            correct = 0,
            explanationBody = "{kw}DNS{/kw}はドメイン名とIPアドレスを相互に対応付ける名前解決の仕組みである。",
            points = listOf(
                Point("DHCP", "IPアドレス等を自動割当てする。"),
                Point("NTP", "ネットワーク経由で時刻を同期する。"),
                Point("SNMP", "ネットワーク機器を監視・管理する。"),
            ),
        ),
        Question(
            id = "net-tcp",
            category = "ネットワーク",
            categoryIcon = R.drawable.ic_cat_lan,
            text = "TCPの説明として最も適切なものはどれか。",
            options = opts(
                "コネクションを確立し、再送制御により信頼性のある通信を行う。",
                "コネクションを確立せず、高速だが信頼性は保証しない。",
                "ブロードキャスト専用で1対多の配信を行う。",
                "経路制御を行いパケットを最適な経路へ転送する。",
            ),
            correct = 0,
            explanationBody = "TCPは3ウェイハンドシェイクで接続を確立する{kw}コネクション型{/kw}プロトコルで、再送制御や順序制御により信頼性を確保する。",
            points = listOf(
                Point("イ", "UDPの説明（コネクションレス）。"),
                Point("ウ", "ブロードキャスト／マルチキャストの特徴。"),
                Point("エ", "IP（ルータ）が担う経路制御の説明。"),
            ),
        ),
        Question(
            id = "net-gateway",
            category = "ネットワーク",
            categoryIcon = R.drawable.ic_cat_lan,
            text = "自分の所属するネットワーク以外の宛先へパケットを送るとき、最初に転送する宛先はどれか。",
            options = opts("デフォルトゲートウェイ", "DNSサーバ", "DHCPサーバ", "プロキシサーバ"),
            correct = 0,
            explanationBody = "宛先が同一ネットワーク外の場合、パケットはまず{kw}デフォルトゲートウェイ{/kw}（ルータ）へ送られ、そこから経路制御される。",
            points = listOf(
                Point("DNSサーバ", "名前解決を行う。"),
                Point("DHCPサーバ", "アドレスを自動割当てする。"),
                Point("プロキシ", "代理でアクセスを中継する。"),
            ),
        ),
        Question(
            id = "net-nat",
            category = "ネットワーク",
            categoryIcon = R.drawable.ic_cat_lan,
            text = "プライベートIPアドレスとグローバルIPアドレスを相互に変換する技術はどれか。",
            options = opts("NAT", "VLAN", "VPN", "QoS"),
            correct = 0,
            explanationBody = "{kw}NAT{/kw}（Network Address Translation）はアドレスを変換し、私設網と公衆網の橋渡しを行う。",
            points = listOf(
                Point("VLAN", "スイッチ上で論理的にLANを分割する。"),
                Point("VPN", "公衆網上に仮想的な専用線を構築する。"),
                Point("QoS", "通信の優先度を制御し品質を確保する。"),
            ),
        ),
        Question(
            id = "net-cidr29",
            category = "ネットワーク",
            categoryIcon = R.drawable.ic_cat_lan,
            text = "プレフィックス長が /29 のサブネットで、ホストに割り当て可能なアドレス数はどれか。",
            options = opts("6", "8", "14", "30"),
            correct = 0,
            explanationBody = "/29 はホスト部が3ビットなので 2^3 − 2 = {kw}6{/kw} 台。ネットワークアドレスとブロードキャストアドレスの2つは除外する。",
            points = listOf(
                Point("/28", "14台（ホスト部4ビット）。"),
                Point("/27", "30台（ホスト部5ビット）。"),
                Point("−2", "ネットワーク／ブロードキャスト分を除く。"),
            ),
        ),
        Question(
            id = "net-mac",
            category = "ネットワーク",
            categoryIcon = R.drawable.ic_cat_lan,
            text = "データリンク層で機器を一意に識別するために用いられるアドレスはどれか。",
            options = opts("MACアドレス", "IPアドレス", "ポート番号", "FQDN"),
            correct = 0,
            explanationBody = "データリンク層ではNICに固有の{kw}MACアドレス{/kw}で機器を識別する。",
            points = listOf(
                Point("IPアドレス", "ネットワーク層の論理アドレス。"),
                Point("ポート番号", "トランスポート層でアプリを識別する。"),
                Point("FQDN", "完全修飾ドメイン名（名前）。"),
            ),
        ),
    )
}
