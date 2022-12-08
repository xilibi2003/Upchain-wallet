# UpChain Wallet - A Powerful & Secure & Open Source Ethereum Android Wallet

[![License](https://img.shields.io/badge/license-GPL3-green.svg?style=flat)](https://github.com/fastlane/fastlane/blob/master/LICENSE)

[<img src=https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png height="88">](https://play.google.com/store/apps/details?id=pro.upchain.ethwallet)

登链钱包是一款安全且功能完善的以太坊去中心化钱包（**完全开源**），界面高度模仿 imToken。

使用登链开源钱包，再也不用担心私钥会不会被上传到服务器（不放心可以自己编译一个）。

遵循开源协议： GPL-3.0

如果觉得不错，点个Star ， 感谢！


## 目录

* [功能介绍](#功能介绍)
* [效果演示](#效果演示)
* [参考的开源项目](#参考的开源项目)
* [关于我](#关于我)


## 功能介绍
- [x] 账户余额查询及转账功能。
- [x] 支持通过生成助记词、Keystore文件、私钥 创建钱包账号。
- [x] 支持导出钱包账号助记词、私钥、Keystore文件。
- [x] 支持多个钱包账号管理
- [x] 贴心的以太坊测试网络（Infura Koven及Ropsten）及本地测试网络 支持
- [x] 支持ERC20 代币（余额显示、转账、代币币价显示）
- [x] 支持用法币（美元和人民币）实时显示币价。
- [x] 历史交易列表显示
- [x] 二维码扫描，兼容imToken 格式
- [x] 支持 DApp Browser  浏览器
- [ ] 地址本（联系人）功能 待完善
- [ ] 比特币及EOS 支持


## 效果演示

<p align="center">
  <img src="https://github.com/xilibi2003/Upchain-wallet/blob/master/img/wallet.gif" width="450">
</p>

**DApp 浏览器** 功能：

<p align="center">
  <img src="https://github.com/xilibi2003/Upchain-wallet/blob/master/img/dapp.gif" width="450">
</p>



安装包下载[地址](https://img.learnblockchain.cn/apk/upchain_wallet.apk)。


## 运行配置

项目使用Android Studio 开发。

创建 local.properties 配置：
```
sdk.dir= Android SDK 目录
gpr.user=
gpr.key=
infura.key=
```

gpr 的配置参考： https://developer.trustwallet.com/wallet-core/integration-guide/android-guide
infura key 用于与节点通信，在 https://www.infura.io/ 申请


## 参考的开源项目

本钱包在开发是站在巨人的肩膀上完成，特别感谢以下项目：

* [web3j](https://docs.web3j.io/index.html)
* [bitcoinj](https://bitcoinj.github.io/javadoc/0.14.7/)
* [Trust-wallet](https://github.com/TrustWallet/trust-wallet-android-source)
* [ETHWallet](https://github.com/DwyaneQ/ETHWallet)
* [BGAQRCode](https://github.com/bingoogolapple/BGAQRCode-Android)

## 关于我

本钱包由登链社区牵头开发，[登链社区](https://learnblockchain.cn)是高质量的中文区块链技术社区，希望我们输出的文章、课程、代码 能推动区块链技术在国内的发展。

感谢[254497767](https://github.com/254497767) 提交 PR ，更新钱包依赖的相关代码库。


加登链社区技术群， 可扫微信二维码：
<p align="center">
  <img src="https://img.learnblockchain.cn/qrcode/xiaona-2.jpg" width="300">
</p>



如果有技术问题，可到[登链社区 - 问答区](https://learnblockchain.cn/questions)提问， 或进入 [Discord](https://discord.gg/hRZrM92hfw) 讨论。

技术合作，可联系我的邮箱(Email): xlb@upchain.pro


你的支持将鼓励我继续提供更好的作品给大家:octocat:

<p align="center">
  <img src="https://learnblockchain.cn/images/qr_pay.jpg" width="300">
</p>







