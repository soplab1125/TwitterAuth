Twitter認証画面
===========
<p><a href="http://www.soplab.net/android.html#NowTracing">NowTracing</a> 開発中にやってみたソースコードになります。<br /></p>
<p>
数多くのサイトでも android のネイティブアプリ で Twitter 認証画面を作る場合のサンプルソースを公開していますが、ワタクシなりにやってみた例を出してみたり、、、(^_^；)
</p>
<p>
NowTracing の場合は、呼び出されたあとにアクセストークンを自作クラスで SharedPreferences に保存し 呼び出し元の画面に戻します。<br />
今回は、SDK をバージョンアップした時に UI がガラっと変わっていたので新しく作りなおしてみましたw

なお、Twitter認証画面 は Twitter4J を使用しています。 

</p>
<p>
ほかサイトでの oAuth 認証の場合にも流用が利きますので参考になればと思います。 (実際に FourSquare の認証もこのソースを流用しています)<br />
</p>

