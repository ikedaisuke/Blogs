# はじめに

この文章は
[Theorem Prover Advent Calendar 2014](http://qiita.com/advent-calendar/2014/theorem_prover)
の 12/24(水) の記事のために書かれました（たぶんね）。

## KeY-project とは

KeY-project とはプログラム、特に Java (の subset) プログラムに対する形式手法のための
ツール群です。The KeY Book (2007) によれば、以下の大学の共同プロジェクトです：

* University of Karlsruhe
* Chalmers University of Technology in Göteborg
* University of Koblenz-Landau

執筆時では以下のツールが開発されているようです：

* KeY Prover (for Java Card)
* KeY-Hoare
* KeYmaera
* KeY for C
* ASMKey

本稿では Key Prover の簡単な紹介を行います。
その他のツールについては、筆者の能力の限界＆アドベントカレンダーの締切のせいもあり、
紹介することができません。
また、KeY Prover の Eclipse IDE plugin や Boland's together CASE tool suite も試していません。

概要を掴むには、
[本家のページ](http://www.key-project.org/)
や
[Wikipedia(en)](http://en.wikipedia.org/wiki/KeY)
などを参照したほうがより正確だと思いますが、
ここ数日で簡単なプログラムの検証（？）をやってみたのでその経験も話します。

# KeY Prover の紹介

KeY Prover は仕様記述言語 Java Modeling Language (JML) をアノテーションとして書いた
Java プログラムの semi-automatic な検証ツールです。
正確には Java のサブセットである Java Card プログラムを対象とします。

JML の代わりに Object Constraint Language (OCL) も使うことができるそうです、
が、試していません。

筆者が理解した Key Prover の構成は以下のようになります：

![img/KeyProver1.png](/20141224-IntroductionToKeYProver/img/KeYProver1.png?raw=true)

![img/KeyProver2.png](/20141224-IntroductionToKeYProver/img/KeYProver2.png?raw=true)

利用者は Java Card のプログラムと JML 仕様記述で書かれたアノテーションを準備します。
あとは、証明ボタンを押すだけです。
証明に成功すれば、Java Card DL に基づいた proof obligation が得られます。
proof obligation は、必ずしもプログラムの「正しさ」を保証するわけではありませんが、
「高信頼性を達成する」ためのひとつの方法です。
動的に行われるテストや契約プログラミングと異なり、静的な検証です。

semi-automatic な検証ツールであるというのは、いくつかの人手が必要だからです。
まず、 JML によるアノテーションは人手で書かなくてはなりません。
特に、ループ不変量とループ変量の記述は自明ではありません。

また、証明に失敗したときは手戻りが必要になります：

* プログラムのデバグ
* JML 記述の見直し
    * ループ不変量は十分か
    * ループ変量は十分か
* Java Card DL の限界は？ (timed out/out of memory)
    * open goal を証明するために利用者が追加する axiom は何か？
	    * axiom を taclet で記述するには？
* SMT ソルバの限界は？ (timed out/out of memory)
* 証明しやすいプログラムと証明しにくいプログラムの違いは？

結局、証明に失敗したときは対話的証明技法と全自動証明技法の両方が必要になるでしょう。
Java Card DL + sequence calculus で導出に失敗したときは open goals が、
SMV ソルバで導出に失敗したときは counter examples が見つかるか、timed out するかなどです。

Java Card DL で導出できるかどうかを判定するには、闇雲にやってみるか、もしくは、
手元で書いた証明を先にしておいて、確信を持って証明ボタンを押すことになります。

Theorem Prover 愛好家にとって、これ以上に面白いことがあるでしょうか。あるかもしれませんが…
Java を日常書かない筆者も、手続き型プログラムの正しさを調べるのは楽しかったです。

SMT ソルバ を用いると、counter examples が生成されます。test cases は z3 が生成します。
そのために SMT ソルバの入出力が読む必要があります。筆者にはできませんでした。
こういう部分は SMT ソルバマニアの知識が生きる場面です。

このように、証明に失敗したときはやることが多いです。
そこで、証明に成功するための必要最小限の記法について、まず説明します。

## Java Card

Java Card は Java のサブセットです。

* [An Introduction to Java Card Technology - Part 1](http://www.oracle.com/technetwork/java/javacard/javacard1-139251.html)
* [Java Card Language (Wikipedea:en)](http://en.wikipedia.org/wiki/Java_Card#Language)

[fmco06post.pdf](http://www.cse.chalmers.se/~philipp/publications/fmco06post.pdf)
によれば、Key Prover が対象とするのは full Java Card 2.2.1 standard だそうです。

筆者は Java と Java Card の違いを理解していませんが、Java のフルセットを検証するのは無茶だろうということは推測できます。

persistent/transient memory model と atomic transactions の検証ができるそうです。

あるメソッドを KeY Prover で検証するためには、そのメソッドが呼び出しているメソッド全てが検証済みでなければなりません。
従って、標準ライブラリ（例えば `import java.io.*`）とかを使っているメソッドは現時点では検証できません。
まずは簡単なメソッドを検証してみましょう。

## JML

Java Modeling Language(JML) は Java プログラムに対する仕様を記述する言語です。
Java プログラム中にコメントとして書きます。

JML でメソッドの

* 事前条件
* 事後条件
* ループ不変量、ループ変量

などを書きます。その他にもいろいろ書きます。

* [The Java Modeling Language](http://www.eecs.ucf.edu/~leavens/JML//index.shtml)
* [JML Reference Manual](http://www.eecs.ucf.edu/~leavens/JML/jmlrefman/jmlrefman.html#SEC_Top)
* [Java Modeling Language (wikipedia:en)](http://en.wikipedia.org/wiki/Java_Modeling_Language)

上記の JML Reference Manual は非常に充実していて便利ですが，
簡単なプログラムの検証に必要な記述はごくわずかですみます。
Wikipedia の記述にある知識だけでもあると便利です。

KeY Prover は JML を拡張した記法も用意しているらしいのですが、筆者は把握していません。

本稿では JML の詳細に深入りせずにシンプルな具体例を提示します。

# Key Prover のインストール

筆者の環境は MacOSX Mavericks です。その他の環境では確かめていないので、あらかじめご了承ください。

KeY Prover をインストールするには、

1. Java Runtime (JRE) をあらかじめ準備し
2. (download)[http://www.key-project.org/download/] を読んで
3. README-(version).txt を読んで
4. KeY-(version).tgz or KeY-(version).zip をダウンロード、展開し
5. required libraries をダウンロードして、適切な場所に展開します

難しいことは何もないと思いますが、よく読まないと step 5. required libraries を忘れます。

## SMT ソルバのインストール

KeY Prover は Java Card DL の sequent calculus の補助ツールとして SMT ソルバを使います。
KeY Prover はサポートする SMT ソルバのバージョンも指定します。
「このバージョンの SMT ソルバは動作を保証しません（警告）」が出ます。
が、筆者は気にせずに最新のバージョンを使ってみました。

現在サポートしている SMT ソルバのうち、次のものを試しました：

### Z3

[Z3](http://z3.codeplex.com/) は、
特に KeY Prover で counter examples を出力するときに必要なのでインストールを勧めます。

Windows にはバイナリがありますが、Linux/MacOSX ではソースコードからビルドする必要があります。

[Z3 latest](http://z3.codeplex.com/SourceControl/latest)
の README をよく読めば難しいことは何もないでしょう。

### Yices

[Yices](http://yices.csl.sri.com/) は各種プラットフォームで動くバイナリを配布しています。

### cvc3

[cvc3](http://www.cs.nyu.edu/acsys/cvc3/) も各種プラットフォームで動くバイナリを配布しています。

バイナリをダウンロードすることを勧めます。

Gentoo Prefix に masked package があったので筆者はそちらを無理矢理インストールしました。
Z3 や yices の ebuild は見つからなかったので、暇ができたらそのうち Overlay に作ります。

### Simplify

一方、

[Simplify](http://kindsoftware.com/products/opensource/Simplify/)

はインストールの方法がよくわからなかったので、試していません。

## 具体例

本稿では KeY Prover を初めて利用する読者のために、シンプルな具体例を提供します：

1. Add
2. ElemIndex
3. Max

全てのファイルはこのリポジトリの [/src](src/) 以下にあります。

### Add

最初にシンプルな JML 記述によるメソッドの振る舞い検証を見ます。
次のプログラムに対する検証を行います。

```Java
public class Add {
  
  public static int add(int x, int y) {
    return x + y;
  }
  
}
```
注：このようなプログラム add を（クラス/メソッド）にすることは、
もちろん実際には推奨されません。しかし、説明のために用意しました。

足し算の可換則と結合則を検証します：

1. x + y == y + x （可換則）
2. (x + y) + z == x + (y + z) （結合則）

事後条件では特別な記述 \\result が使えます。
メソッド add に対する \\result は x + y です。
というのは、メソッド add にreturn x + y; という文があるからです。

そこで、可換則の記述は次のようになります：

```Java
/*@ ensures y + x == \result; @*/
```

一方、事後条件にメソッドの呼び出しも使えます。
そこで、結合則を書くときには \\result の代わりにメソッド呼び出しを使ってみましょう：

```Java
/*@ ensures (\forall int z; add(x, y) + z == x + add(y, z)); @*/
```

このように、一般に調べたい事後条件を JML で書く方法は一通りではありません。
その代わりに、

```Java
/*@ ensures add(add(x, y), z) == (blah blah blah); @*/
```

と書くこともできます。

さて、事後条件はまとめて書くことができます。KeY Prover への入力は最終的に次のようになります：

```Java
public class Add {
  
  /*@ ensures y + x == \result; // commutative
    @ ensures (\forall int z; add(x, y) + z == x + add(y, z)); // associative
    @*/
  public static int add(int x, int y) {
    return x + y;
  }
  
}
```

では、実際に KeY Prover での検証過程を見ていきましょう。

#### KeY Prover を起動

KeY Prover を起動すると、次のようなウィンドウが現れます：

![img/Add/start.png](/20141224-IntroductionToKeYProver/img/Add/start.png?raw=true)

この小さいウィンドウをリサイズして大きくしておきます。

![img/Add/resized.png](/20141224-IntroductionToKeYProver/img/Add/resized.png?raw=true)

#### ファイルをロード

![img/Add/prepare.png](/20141224-IntroductionToKeYProver/img/Add/prepare.png?raw=true)

左の Contract Targets に事後条件を記述したメソッドが現れます。
今回は add だけですが、複数のメソッドの証明をするときには、個々を選択する必要があります。
選択を忘れると、先頭のメソッドの検証だけをするので注意が必要です。

右には選択されたメソッドに対する Contract が現れます。

Start Proof のボタンを押すと、いよいよ自動証明を行う前のウィンドウが現れます。　

#### 自動証明を始める

![img/Add/go.png](/20141224-IntroductionToKeYProver/img/Add/go.png?raw=true)

左上のいかにも処理を始めそうなボタンを押せば、自動証明が始まります。

その右横に用いる SMT ソルバの選択ボタンがあります。
Java Card DL + sequent calculus とは別に、SMT ソルバを用いたいときはそちらを押します。

左には 0 OPEN GOAL があります。
右には Current Goal があります。
Java では実行時例外が発生する可能性があるため、
検証式には例外に関するいろいろな条件がつきますが、
今回の検証では次の式が本質です：

    (blah blah blah)
    -> javaAddInt(y, x) = result
	   & \forall int z;
	       (   inInt(z)
		    -> javaAddInt(add(x, y), z) = javaAddInt(x, add(y, z))))
       & (blah blah blah)

この式は KeY Prover が JML アノテーションと Java Card プログラムから自動的に変換したもので、
手入力する必要はありません。

Proof Search Strategy ボタンを押すと、SMT ソルバに与えるメモリの上限などを変更できるのですが、
今回の場合はデフォルトで証明が終わるので、変更は必要ありません。

#### 結果

証明が成功すると、次の二つのウィンドウが現れます：

![img/Add/proved.png](/20141224-IntroductionToKeYProver/img/Add/proved.png?raw=true)

![img/Add/result.png](/20141224-IntroductionToKeYProver/img/Add/result.png?raw=true)

最初のウィンドウには、実行時間の統計情報などが現れます。
結果のウィンドウには、
左に自動証明で用いた推論規則の一覧が、
右に推論が進んだ式が現れます。

各推論規則はクリックできて、推論がどのように進むかを確かめることができます。
証明に成功したときは、推論規則の最後に緑色の Closed goal が現れます。
今回は成功しましたが、失敗するときはいくつかの Open goal(s) が現れます。

add は副作用を持ちません。このときは、`/*@ pure @*/` を明示的につけます：

```Java
public static /*@ pure @*/ int add(int x, int y)
```

`/*@ pure @*/` はプログラム中にループがあるときの検証を楽にするものです。
次節で紹介するループ不変量の記述にできることが増えます。
今回の検証にはループを持つプログラムが全体にないので必要ありませんが、
覚えておくとよいです。

今回の事例では推論は sequent calculus でできました。

一方、 SMT ソルバにやらせると

* Z3 : found a counter example
* Yices : timeout
* cvc3 : found a counter example

となります。これが果たして KeY Prover のサポートするバージョンでなかったせいなのか、
他の原因があるのかよくわかりません。

[src/Add/README.md](src/Add/README.md) に Z3 と Yices の入力と出力を書いておいたので、
識者に検証していただきたいです。

以上、本節は、事後条件の書き方を紹介することが目的でした。
本節以降は、ウィンドウなどの画面を引用しません（どれも似たようなものなので）。

### ElemIndex

KeY の配布物には examples が含まれています。
その中(04-LinkedList)に linked list の search の検証があります。
ここでいう search は、リスト中に与えられた要素が存在するかどうかを調べるプログラムです。

本節では linked list の代わりに、よりシンプルに、 Java Card の配列に対して同じことを調べます。
また、search という名前は曖昧だと感じたので Haskell に倣って elemIndex と命名しました。
elemIndex は、配列の中に与えられた要素が存在しなければ -1 を、
存在するとき、そのなかで最小の index を返します。

検証対象のプログラムは以下の通りです：

```Java
public class ElemIndex {
  
  public static int elemIndex(int x, int[] a) {
    int i = 0, r = -1;

    while (r == -1 && i < a.length) {
      if (a[i] == x) r = i; else i++;
    }
    return r;
  }
  
}
```

このプログラムを検証するときは、事前条件と事後条件の他に、
ループ不変量とループ変量の二つを書く必要があります。
ループ不変量はループに入る前とループに入った後の条件を検証するために必要です。
ループ変量はループが停止することを保証するために必要です。

ともあれ、事前条件と事後条件を書いてみましょう。

```Java
  /*@ requires a != null;
    @ ensures
    @   (\result == -1 &&
    @     (\forall int j; 0 <= j && j < a.length; a[j] != x)) ||
    @     (0 <= \result && \result < a.length && a[\result] == x &&
    @        (\forall int j; 0 <= j && j < \result; a[j] != x));
    @*/
  public static /*@ pure @*/ int elemIndex(int x, int[] a) {
    (blah blah blah)
```

前節 add の諸性質に比べるとずいぶん長くなりましたが、個々に見ていけば複雑ではありません。

まず、事前条件として `a! = null` が必要です。
プログラム elemIndex に `a == null` の場合を追加すれば、条件は変わりますが、
ここでは説明を簡単にするために、それを考えません。

`int x` は Java では primitive type なので `null` になることはありません。

次に事後条件を書きます。
配列 `a` の中に `x` が存在しないとき、または `x` が存在しないときで場合分けをします：

```Java
/*@ ensures
    (x が存在しないとき) || (x が存在するとき) // 疑似記述
  @*/
```
`x` が存在しないときは、

* `\result == -1`
* `(\forall int j; 0 <= j && j < a.length; a[j] != x)`

の両方をみたします。これらを `&&` でつなぎます。

一方、`x` が存在するときは、

* `(0 <= \result && \result < a.length)`
* `a[\result] == x`
* `(\forall int j; 0 <= j < \result; a[j] != x)`

の全てが成り立ちます。最後の式は index の最小性を意味します。

以上のことから、事後条件は次のようになります：

```Java
  /*@ requires a != null;
    @ ensures
    @   (\result == -1 &&
    @     (\forall int j; 0 <= j && j < a.length; a[j] != x)) ||
    @     (0 <= \result && \result < a.length && a[\result] == x &&
    @        (\forall int j; 0 <= j && j < \result; a[j] != x));
    @*/
  public static /*@ pure @*/ int elemIndex(int x, int[] a)
    (blah blah blah)
```

次にループ不変量を書きます。ループ不変量とはループ中に変わらない条件のことで、
かつ、事前条件と事後条件を導出するために必要な条件でなければ検証ができません。
導出のためにどのようなループ不変量を書いたらいいのかは、自明ではないのですが、
まあともあれ、やってみましょう。今回は試行錯誤せずに正答を与えます。

ループ中は次のことが成り立っています：

* `a != null`
* `0 <= i <= a.length`

ここで、 `i < a.length` ではなく `i <= a.length` であることに注意してください。
while の終了条件で `i < a.length` ですが、ループ中では `i` は `a.length` になり得ます。
実際、`i < a.length` のまま Key Prover に渡すと証明ができずに open goal が残ります。

事前条件と事後条件を導出するためのループ不変量はこれだけでは足りません。
次の条件のどちらかも必要です：

1. `r == -1`
2. `r == i && i < a.length && a[r] == x`

配列 `a` の中に `x` が存在する場合と、存在しない場合です。

最後に、ループ中に代入しうるローカル変数を明示します。
今回は `r` と `i` です。このとき、`/*@ assignable r, i; @*/` と書きます。

最終的に、（導出を成功させる）ループ不変量の記述は次のようになります：

```Java
    /*@ loop_invariant
      @   a != null && 0 <= i && i <= a.length &&
      @   (\forall int j; 0 <= j && j < i; a[j] != x) &&
      @   (r == -1 || (r == i && i < a.length && a[r] == x));
      @ assignable r, i;
      @ (blah blah blah)
	  @*/
```

次に、ループ変量を書きます。ループ変量はループ中に減る値で、
かつループが終了することを導出するものでなければなりません。
ループ不変量と同様に、検証に成功するためのループ変量の記述は自明ではありませんが、
ここでは正答を示します。

ループ中では `r == -1` を満たす限り `i` がどんどん増えていきます。
なので、減る値は `a.length - i` です。
一方、`r != -1` のときは減る値はありません。
以上のことからループ変量は `/*@ decreases r == -1 ? a.length - i : 0` @*/ になります。

これで検証に必要な JML アノテーションの記述は終わりです。
最終的なプログラムは以下の通りです：

```Java
public class ElemIndex {

  /*@ requires a != null;
    @ ensures
    @   (\result == -1 &&
    @     (\forall int j; 0 <= j && j < a.length; a[j] != x)) ||
    @     (0 <= \result && \result < a.length && a[\result] == x &&
    @        (\forall int j; 0 <= j && j < \result; a[j] != x));
    @*/
  public static /*@ pure @*/ int elemIndex(int x, int[] a) {
    int i = 0, r = -1;

    /*@ loop_invariant
      @   a != null && 0 <= i && i <= a.length &&
      @   (\forall int j; 0 <= j && j < i; a[j] != x) &&
      @   (r == -1 || (r == i && i < a.length && a[r] == x));
      @ assignable r, i;
      @ decreases r == -1 ? a.length - i : 0;
      @*/
    while (r == -1 && i < a.length) {
      if (a[i] == x) r = i; else i++;
    }
    return r;
  }

}
```

今回の検証も SMT ソルバを用いるまでもなく Java DL と sequent calculus のみで、
導出が終わります。
本節では、Java DL は配列の検証を行うことが可能であることを紹介しました。
筆者の知る限り、Hoare logic ではそこまでの能力はありません。

### Max

# KeY Prover を支える技術

ここまで、簡単な具体例を見てきました。
これは料理番組でいえば、材料や調理法を見せた後で、
「出来上がりはこちらになります」と盛りつけた皿を見せるようなものです。

この文中には十分に説明していなかったキーワードや技術がたくさんでてきたので、
そろそろこの節で簡単な説明をします。

## Hoare logic

[Hoare](http://en.wikipedia.org/wiki/Tony_Hoare) は人名です。
[Hoare logic](http://en.wikipedia.org/wiki/Hoare_logic) は別名 Floyd-Hoare logic とも呼ばれ、
整数演算と代入、if、連接、そして
while ループのみでできあがる簡単な手続きプログラミング言語(WHILE プログラム)の検証を行うことができます。

日本語の文献はたくさんありますが、筆者が最近読んだものは

* [数学セミナー 2014年11月号 鹿島亮 ホーア論理・ダイナミック論理 p.26 - 31](http://www.nippyo.co.jp/magazine/6653.html)
* [形式手法入門 ロジックによるソフトウェア設計](www.amazon.co.jp/dp/4274211886/)

です。オンラインで手に入る文献も探せばあるでしょう。

Hoare logic の特徴は、プログラムに対して事前条件と事後条件を人間が与えること、
そしてループ不変量やループ変量も人間があたえることで、事前条件と事後条件を導出できることにあります。
soundness や completeness が成り立つことについては、専門的な文献をあたってください。

### first order dynamic logic

Hoare logic では、例えば配列を扱うことを考えていません。
Hoare logic の拡張はいろいろあるらしいですが（筆者は知りません）、
そのひとつが first order dynamic logic です。

* [数学セミナー 2014年11月号 鹿島亮 ホーア論理・ダイナミック論理 p.26 - 31](http://www.nippyo.co.jp/magazine/6653.html)
* [Dynamic logic (Wikipedia:en)](http://en.wikipedia.org/wiki/Dynamic_logic_%28modal_logic%29)
* [Reasoning about Programs with Dynamic Logic - Part II (slide:pdf)](https://www.tu-braunschweig.de/Medien-DB/isf/sse/12_dynamiclogiccalculus_vl.pdf)

Wikipedia を見てもらえれば分かりますが、説明するだけの紙面はここにはありません。
例えば上記の Schaefer 先生のスライドでも見てください。

鹿島先生の記事に propositional dynamic logic の説明があります。
first order dynamic logic はそれに \\forall と \\exists が付け加えられたようなものです（いい加減な説明）。

#### Java Card DL

* [Introduction to JavaCard Dynamic Logic (slide:pdf)](https://lfm.iti.kit.edu/download/javaDL.pdf)
* [08: Reasoning about Java Programs with Dynimaic Logic (slide:pdf)](http://symbolaris.com/course/dcd/08-JavaDL.pdf)

first order dynamic logic の説明も満足にしていないのに、Java Card DL を説明するのはそもそも無理があります。
Java Card DL は first order dynamic logic のバリアントだそうです。

## sequent calculus

* [sequent calculus (wikipedia:en)](http://en.wikipedia.org/wiki/Sequent_calculus)
* [Verifying Object-Oriented Programs with KeY: A Tutorial](http://www.cse.chalmers.se/~philipp/publications/fmco06post.pdf)

論理式が正当であることの導出方法にはいろいろあるわけで、その一つが sequent calculus です。
KeY Prover が用いる Java Card DL に対する導出方法は sequent calculus であるという説明を読みました。

## SMT ソルバ

検索したらいろいろでてきたので、適当に並べてみました：

* [SMT (wikipedia:en)](http://en.wikipedia.org/wiki/Satisfiability_Modulo_Theories)
* [すえひろがりっっっっ! SMT solver 入門](http://d.hatena.ne.jp/suer/20081125/1227634082)

前にも書きましたが、筆者は SMT solver について何も知りません。

## taclets

Java Card DL の sequent calculus に用いる推論規則は taclets 記法で書きます：

* [Taclets and the KeY Prover(pdf)](http://www.informatik.uni-bremen.de/uitp03/entcs/04-Giese.pdf)
* [Taclets: a new paradigm for constructing interactive theorem provers](http://www.rac.es/ficheros/doc/00147.pdf)

KeY Prover は term をクリックしてメニューがでたり、ドラッグアンドドロップで規則を適用したり、
ユーザが新しく taclet を追加したりして open goals を解決できるそうです。
が、試していません。

# プログラムは書いた通りに動く

KeY Prover の説明を（いいかげんに）書いていたら、
読者によっては誤解を招く部分もあるかもしれないと危惧したので余計なコメントも書きます。
Advent の趣旨からは外れるかもしれませんが、なんとかまとめるので聞いてやってください。

Key Prover だけでなく KeY project の目標は The KeY book によれば

> Long gone are the days when program verification was a task carried out
> merely by hand with paper and pen.
> (snip)
> In addition to the engineering effort required to build a program verification
> tool, building and using such a tool require many skills from computer science,
> logic, and mathematics. To advance the field of program verification, we must
> facilitate the acquisition of these skills for our colleagues, students, and
> other tool builders.
> (snip)
> The ultimate goal of program verification is not the theory behind the tools
> or the tools themselves, but the application of the theory and tools in the
> software engineering process. Our society relies on the correctness of a vast
> and growing amount of software. Improving the software engineering process is
> an important, long-term goal with many steps. Two of those steps are the KeY
> tool and this KeY book.

と締めくくられています。このことは、中島先生の「形式手法入門」にある、
「高い信頼性を達成する手段」の説明と共通しています。

> 前提 : 設計方法論 + 数理論理 + ドメイン知識
> 形式手法 = 形式仕様言語 + 形式検証の方法 + 支援ツール
> 結果 : 求められる信頼性の基準

> 形式手法が工学的な道具となるには、
> 形式仕様作成ならびに形式検証といった作業を支援するツールが必須となる。
> 特に、記述の形式検証に関わる技術は形式手法の特徴であり、
> 適切な支援ツールの開発が実用化の要となる。
> 人手の証明に頼る方法では工学的な道具になり得ない。

両者に共通するのは、支援ツールが大事ということです。
KeY Prover の持つ、プログラムにコメントを書いてボタンを押すだけという automation の機能は
十分に工学的な道具になっているでしょう。

それは認めましょう。しかし、プログラム検証の ultimate goal を達成するために、
現状の形式手法というアプローチは果たして有効なのか？という疑問を持っています。
計算機の力を借りて証明しなくても、技術を持ったプログラマが集まってレビューすればいいんじゃないの？
（ついでにテストや動的な契約プログラミングで穴を探す）
コストが高いというのは工学的な発想ではないのではないかなー。

theorem prover を触ったり理解したり理論の発展を眺めること自体は楽しいんですよ。

## プログラムは信頼されなければならないか

プログラムは仕様と実装が複雑になるにつれて、人間が作ろうとしたものとできあがったものとの間に、
ずれが生ずる。これはプログラムに限ったことではなく、全ての創作に言えることである。

プログラムは書いたものが、アーキテクチャの上で、書いた通りに動く（コンパイラのバグ等細かいことは言わない）。
これは現象である。「プログラムは信頼されなければならない」というフレーズには、
善悪・希望の問題が含まれている。少しでも気に入らぬことがあるとすぐけしからんとくる。
プログラムは今や各人の生活や安危に直接にひびくことがあるから、
単に現象を後から観察するだけですませることはできない。
善悪の判断を入れるのは当然である、という人もいるかもしれない。
ましてエンジニアならそうであろう。
しかし、けしかるけしからんの一歩前に現象をよく見るという見方もあってよいのではないか。
判断はその次に続ければよい。

プログラムの高信頼性とはなんであろうか。99% のプログラムにバグを生じさせない技術が将来できたとしても、
たったひとつのバグが起きれば信頼など吹き飛んでしまう。

プログラム検証の目指す ultimate goal は高い。しかも、その目標を達成する技術が学際的・属人的であるので、
研究の説明をするときに、つい「高信頼性を達成するために XXXX …」という前書きがつく。

KeY Prover は十分に完成されたツールであり、使いやすいインターフェイスを備え、
(わりと)最新の数理技術(Java Card DL)を背景に持つ。
にもかかわらず、検証できるプログラムはとても少ない。

まず、現実の客観的な観察が必要であろう。

# おわりに

筆者の Twitter account は @ikegami__ です。
体調が良いときしか見ませんが、簡単なコメントはこちらにくださっても結構です。

# 謝辞

この記事を書いた理由は、2014 年の Theorem Prover Advent もあるのですが、
その他に二つあります。

一つは、前にも参照しましたが、鹿島亮先生の数学セミナーの記事です。
長くなりますが、ここに引用します：

> 2.5 おわりに
> 
> ダイナミック論理について個人的(引用注：鹿島先生)な印象をいくつか述べさせていただく。
> ホーア論理はプログラム検証に関する必須の基礎知識とみなされているようだが、
> ダイナミック論理は現在の日本では認知度が低いようだ
> （それぞれのキーワードでインターネット検索をしてみると違いがよくわかる）
> この理由はプログラムの正当性をダイナミック論理で示そうとすると
> PDL(引用注: propositional dynamic logic) ではなく　
> FODL(引用注: first order dynamic logic) を使う必要があるが、
> 正当性の証明ならわざわざ難しい FODL を持ち出さなくてもホーア論理で十分だから、
> ということだろうか？
> 
> 実際には FODL を使用した検証システム（たとえば KeY）もあるようだ。
> 現実のプログラム検証で何がどこまでできるのか、
> 筆者(引用注: 鹿島先生)には知識がないのでぜひとも識者に解説をお願いしたいと思う。
> [後略]

僕は識者じゃないんですけど、この文を読んで、数日は費やしてみるかという気になりました。

もう一つの理由について。序文に書いたように KeY Project は Chalmers University も協力しています。
僕は前職にいたときに Chalmers を訪問したことがあって、KeY team の内輪向けの発表を何度か聞いています。
発表は必ずしも英語とは限らなくて、僕はスウェーデン語はよくわからなかったし、
dynamic logic とか知らなかった（今も知りませんが）にもかかわらず、
メンバーの楽しそうな雰囲気は伝わりました。

KeY Prover で検証できるプログラムは本当にささやかなものですが、(実際 The KeY Book にもそう書いてあります)
教育目的、あるいは将来、検証技術が発展したときに用いるインターフェイスの観点からはよくできていると思います。

本来の滞在目的は KeY Prover の話を聞くことではなかったんですけど、
それを許してくれた前職のシステム検証研究センターならびに、
経済的な支援をしていただいた CREST プログラム(平成14年度採択課題)「情報社会を支える新しい高性能情報処理技術」
には大変感謝しております。
当該 CREST プロジェクトでは、「対話型定理証明支援系にモデル検査器をつなぐ」という仕事に従事していました。
KeY Prover は、「(Java Card プログラムを検証するために)
semi-automatic 証明支援系に SMT ソルバをつなぐ」というツールとも思えるので、
今となっては関連した話を聞いていたのだなあという話。

# 参考文献
