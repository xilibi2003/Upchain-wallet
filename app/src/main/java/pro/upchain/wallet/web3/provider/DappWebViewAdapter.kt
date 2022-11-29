package pro.upchain.wallet.web3.provider

import android.content.Context
import android.graphics.Bitmap
import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.AutoCompleteTextView
import com.trust.web3.demo.WebAppInterface
import kotlinx.android.synthetic.main.item_autocomplete_url.view.*
import org.jetbrains.annotations.NotNull
import org.web3j.crypto.Credentials
import org.web3j.crypto.WalletUtils
import pro.upchain.wallet.R
import pro.upchain.wallet.ui.fragment.DappBrowserFragment
import pro.upchain.wallet.viewmodel.DappBrowserViewModel
import pro.upchain.wallet.web3.OnSignTransactionListener
import kotlin.math.log


class DappWebViewAdapter(
    @NotNull val context: Context,
    @NotNull val webView: WebView,
    @NotNull val viewModel: DappBrowserViewModel,
    @NotNull val urlTv: AutoCompleteTextView,
    @NotNull val dappBrowserFragment: DappBrowserFragment,
    @NotNull val onSignTransactionListener: OnSignTransactionListener
) {

    companion object {
        private const val DAPP_URL = "http://192.168.0.101:8080/"
        private const val CHAIN_ID = 0x61
        private const val RPC_URL = "https://data-seed-prebsc-2-s1.binance.org:8545/"
    }

    fun init() {
        WebView.setWebContentsDebuggingEnabled(true)
        webView.settings.run {
            javaScriptEnabled = true
            domStorageEnabled = true
        }

        // 加载指定位置的钱包
        // 加载指定位置的钱包
        val credentials: Credentials =
            WalletUtils.loadCredentials(
                viewModel.defaultWallet().value?.password,
                viewModel.defaultWallet().value?.keystorePath
            )
        val address: String = credentials.address
        println("打印多余的信息")
        println("address:$address")
        println("PrivateKey:" + credentials.getEcKeyPair().getPrivateKey())
        println("PublicKey:" + credentials.getEcKeyPair().getPublicKey())

        WebAppInterface(context, webView, DAPP_URL, viewModel, dappBrowserFragment,onSignTransactionListener).run {
            webView.addJavascriptInterface(this, "_tw_")
            val webViewClient = object : WebViewClient() {

                override fun onLoadResource(view: WebView?, url: String?) {
                    super.onLoadResource(view, url)

                    //设置trust_min.js
                    webView.evaluateJavascript(loadProviderJs(), null)
                    //设置js初始化语句
                    println("初始化脚本 onLoadResource")
                    webView.evaluateJavascript(
                        loadInitJs(
                            CHAIN_ID,
                            RPC_URL
                        ), null
                    )
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)

                    //设置trust_min.js
                    webView.evaluateJavascript(loadProviderJs(), null)
                    //设置js初始化语句
                    webView.evaluateJavascript(
                        loadInitJs(
                            CHAIN_ID,
                            RPC_URL
                        ), null
                    )

                }


                override fun onReceivedSslError(
                    view: WebView?,
                    handler: SslErrorHandler?,
                    error: SslError?
                ) {
                    // Ignore SSL certificate errors
                    handler?.proceed()
                    println(error.toString())
                }
            }
            webView.webViewClient = webViewClient
            webView.loadUrl(DAPP_URL)
        }
    }

    private fun loadProviderJs(): String {
        return context.resources.openRawResource(R.raw.trust).bufferedReader().use { it.readText() }
    }

    private fun loadInitJs(chainId: Int, rpcUrl: String): String {
        val source = """
        (function() {
            var config = {                
                ethereum: {
                    chainId: $chainId,
                    rpcUrl: "$rpcUrl"
                },
                solana: {
                    cluster: "mainnet-beta",
                },
                isDebug: true
            };
            trustwallet.ethereum = new trustwallet.Provider(config);
            trustwallet.solana = new trustwallet.SolanaProvider(config);
            trustwallet.postMessage = (json) => {
                window._tw_.postMessage(JSON.stringify(json));
            }
            window.ethereum = trustwallet.ethereum;
        })();
        """
        return source
    }

}