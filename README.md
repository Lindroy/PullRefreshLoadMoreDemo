# 上拉加载下拉刷新


## 前言

本文并非是要造轮子（第三方库），而是要利用好已有的轮子，减少模板代码，力求写出优雅的代码。

一个列表类页面离不开如下三种工具：

1. 适配器（Adapter）
2. 上拉加载下拉刷新
3. 多状态视图

每次处理列表页面时总是要适配数据、判断视图的状态、判断是否可以上拉或者上拉……这样对导致许多重复的代码，所以有必要抽取封装，尽量一个操作用一行代码解决。

## 1、框架选择

### 1.1 适配器

[BaseRecyclerViewAdapterHelper](https://github.com/CymChad/BaseRecyclerViewAdapterHelper "BaseRecyclerViewAdapterHelper")（简称BRAH）除了一般Adapter的功能外，还提供了上拉加载更多的功能，可以配合其他的下拉刷新控件（比如原生的`SwipeRefreshLayout`）使用。

**BRAH**还提供了以下两个方法来处理请求到的数据：

- `setNewData()`：设置新的数据源，比如列表第一页的数据；
- `addData()`：添加新的数据，此时页面中也有数据。

这样可以统一把数据交给**BRAH**托管，有效避免数据源不同引起的刷新失败问题。

### 1.2 多状态视图

#### 1.2.1 全局配置
 [MultipleStatusView](https://github.com/qyxxjd/MultipleStatusView "MultipleStatusView")的使用可以参考GitHub上的文档，为了整个App中的视图布局统一，可以创建与库中名称相同的layout文件，然后改写里面的布局。注意里面的控件id安装文档的命名就可以了。

#### 1.2.2 方法封装

多状态视图只跟数据状态有关，所以可以优先封装。它有以下5种布局：

- 加载视图（loading）：正在从网络请求数据
- 内容视图（content）：请求成功且有数据
- 空视图（empty）：请求成功但没有数据
- 断网（no_network）：由于网络断开导致请求失败
- 内容视图（content）：网络连接但是服务器出错导致请求失败

当用户首次打开列表页面时，其显示流程可参见下图：

[![多状态视图](https://raw.githubusercontent.com/Lindroy/PullRefreshLoadMoreDemo/master/screenshot/%E5%A4%9A%E7%8A%B6%E6%80%81%E8%A7%86%E5%9B%BE.png "多状态视图")](https://raw.githubusercontent.com/Lindroy/PullRefreshLoadMoreDemo/master/screenshot/%E5%A4%9A%E7%8A%B6%E6%80%81%E8%A7%86%E5%9B%BE.png "多状态视图")


从上图可以看出，空视图和内容视图的显示取决于网络请求成功之后是否有数据，故这两个方法可以封装到一个`showSuccessView()`方法中（下文将统称为**成功视图**），在网络请求成功的回调中调用；断网视图和服务器错误视图的显示取决于网络请求失败后是否有网络，故这两个方法可以封装到一个`showFailedView()`方法中（下文将统称为**失败视图**）。

综上，多状态视图可以简化如下：

- 加载视图（loading）：正在从网络请求数据
- 成功视图（success）：请求成功
- 失败视图（fail）：请求失败

封装成以下方法：
```kotlin
/**
 * 显示加载中视图
 */
fun MultipleStatusView.showLoadingView() {
    showLoading()
}

/**
 * 显示空视图
 */
fun MultipleStatusView.showEmptyView() {
    showEmpty()
}

/**
 * 显示内容视图
 */
fun MultipleStatusView.showContentView() {
    showContent()
}

/**
 * 请求成功，分为内容视图和空视图
 */
fun MultipleStatusView.showSuccessView(isEmptyData: Boolean) {
    if (isEmptyData) {
        showEmptyView()
    } else {
        showContentView()
    }
}

/**
 * 请求失败，分为错误和网络断开
 * @param isOldDataEmpty: 页面中是否已存在数据，传入true时不会显示失败视图
 */
@JvmOverloads
fun MultipleStatusView.showFailedView(isOldDataEmpty: Boolean = false) {
    if (isOldDataEmpty) {
        when (context.isNetworkConnect()) {
            true -> showError()
            false -> showNoNetwork()
        }
    }
}
```
### 1.3 上拉加载和下拉刷新框架
[SmartRefreshLayout](https://github.com/scwang90/SmartRefreshLayout "SmartRefreshLayout")提供了丰富的上拉加载和下拉刷新样式，以及上拉和下拉的回调。如果想在数据没有填满一页时禁止上拉加载，则可将`setEnableLoadMoreWhenContentNotFull()`的参数设为false。

## 2、思路分析

### 2.1 多状态视图与上拉加载下拉刷新框架的配合

一表胜千言。多状态视图与上拉加载下拉刷新框架的配合使用详见下表：

[![归纳图](https://raw.githubusercontent.com/Lindroy/PullRefreshLoadMoreDemo/master/screenshot/sheet.png "归纳图")](https://raw.githubusercontent.com/Lindroy/PullRefreshLoadMoreDemo/master/screenshot/sheet.png "归纳图")

- 页面没有数据或数据没有填满一页时，都不能上拉加载；
- 空视图能否下拉刷新取决于数据的来源，如果本页数据都是由本机用户产生的，比如收藏列表页，列表数据需要本机用户离开当前页面去收藏信息才有数据，那么下拉刷新显然不会有新数据的，故可以不必下拉刷新；反之，本页数据可以来自于其他用户的，比如评论列表，那么则应该支持下拉刷新。对此，后面会在代码手动控制空视图是否可以下拉刷新；
- 鉴于目前我实际项目中遇到的失败视图都有重试按钮，故为了节省篇幅，本文中的失败视图都是通过点击重试按钮重新加载数据，不能下拉刷新。

由于空视图时也可能下拉刷新，故在布局中，[MultipleStatusView](https://github.com/qyxxjd/MultipleStatusView "MultipleStatusView")应在[SmartRefreshLayout](https://github.com/scwang90/SmartRefreshLayout "SmartRefreshLayout")内部：

```xml
    <com.scwang.smartrefresh.layout.SmartRefreshLayout
        android:id="@+id/rfMovie"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <com.classic.common.MultipleStatusView
            android:id="@+id/statusView"
            android:layout_width="match_parent"
            android:layout_height="match_parent">

            <android.support.v7.widget.RecyclerView
                android:id="@+id/rvMovie"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                app:layoutManager="android.support.v7.widget.LinearLayoutManager">

            </android.support.v7.widget.RecyclerView>

        </com.classic.common.MultipleStatusView>
    </com.scwang.smartrefresh.layout.SmartRefreshLayout>
```

### 2.2 请求失败时的原有数据处理

为了更好的用户体验，在下拉刷新或者下拉加载请求数据失败时，如果页面中已有数据，则应予以保留，另以其他形式(如Toast)提示用户。

### 2.3 上拉加载和下拉刷新的区分
网络请求数据完成之后，我们需要在请求成功或者失败的回调中处理数据或页面布局，那么如何判断用户操作了上拉加载和下拉刷新呢？

由于列表的数据是分页请求的，这样上拉加载和下拉刷新就有了一个重要的区分依据：下拉刷新是从首页请求数据，页码为1，而下拉加载是在原有数据的底部添加数据，页码必不为1。故我们可以根据页码（pageNo）的值分开处理上拉加载和下拉刷新。

### 2.4 本地加载完毕和所有数据加载完毕的区分

除非你的列表像知乎或者今日头条那样具有海量的数据，否则用户不断上拉加载，数据总有加载完的时候。这样一来就需要判断本次上拉加载是不是加载到列表的底部了。

#### 2.4.1 通过比较新数据数目与pageSize区分

分页加载时，每页加载的数据数目（`pageSize`）是固定的，假设`pageSize`为20，如果上拉加载到数据有20条时，我们就可以先假定列表还有数据，可以允许下次上拉加载；反之，如果数据不足20条，那么就应告知用户列表中所有的数据均已加载完毕，不能再上拉加载了。

#### 2.4.2 通过判断本次加载的数据是否为空

这个比2.4.1中的判断简单，如果出现加载的新数据为空，则立即判定列表中所有的数据均已加载完毕。但不足之处是必须加载到数据为空为止（比2.4.1多加载一次）。

#### 2.4.3 思路分析总结

综合前面的分析，整个过程可用如下流程图表示：

[![上拉加载下拉刷新流程图](https://raw.githubusercontent.com/Lindroy/PullRefreshLoadMoreDemo/master/screenshot/%E4%B8%8A%E6%8B%89%E5%8A%A0%E8%BD%BD%E4%B8%8B%E6%8B%89%E5%88%B7%E6%96%B0%E6%B5%81%E7%A8%8B%E5%9B%BE.png "上拉加载下拉刷新流程图")](https://raw.githubusercontent.com/Lindroy/PullRefreshLoadMoreDemo/master/screenshot/%E4%B8%8A%E6%8B%89%E5%8A%A0%E8%BD%BD%E4%B8%8B%E6%8B%89%E5%88%B7%E6%96%B0%E6%B5%81%E7%A8%8B%E5%9B%BE.png "上拉加载下拉刷新流程图")

## 3、具体实现

### 3.1 SmartRefreshLayout实现上拉加载和下拉刷新

#### 3.1.1 所用框架

- 上拉加载：[SmartRefreshLayout](https://github.com/scwang90/SmartRefreshLayout "SmartRefreshLayout")
- 下拉刷新：[SmartRefreshLayout](https://github.com/scwang90/SmartRefreshLayout "SmartRefreshLayout")
- 多状态视图：[MultipleStatusView](https://github.com/qyxxjd/MultipleStatusView "MultipleStatusView")
- 适配器：[BaseRecyclerViewAdapterHelper](https://github.com/CymChad/BaseRecyclerViewAdapterHelper "BaseRecyclerViewAdapterHelper")

#### 3.1.2 请求成功

首先来看请求成功的情况，前面说过请求成功时分为有数据和无数据两种情况。假如数据为空，也就是显示空布局时，需要根据实际情况控制是否可以下拉刷新，这里可通过`canEmptyRefresh`来控制。

```kotlin
/**
 * SmartRefreshLayout实现下拉刷新和上拉加载
 * 请求成功时刷新列表
 * @param canEmptyRefresh: 页面数据为空时是否允许上拉和下拉
 */
fun <T : Any> SmartRefreshLayout.refreshWhenSuccess(
    adapter: BaseQuickAdapter<T, BaseViewHolder>,
    newData: List<T>,
    pageNo: Int,
    canEmptyRefresh: Boolean = true,
    pageSize: Int = 20
) {
    when (pageNo == 1) {
        true -> {
            adapter.setNewData(newData)
            //是否可以下拉刷新，综合canEmptyRefresh和页面数据是否为空判断
            val enableRefresh = canEmptyRefresh || adapter.data.isNotEmpty()
            setEnableRefresh(enableRefresh)
            if(enableRefresh){
                //如果上一步没有禁止下拉刷新，此处应该停止下拉刷新
                finishRefresh()
            }
            //没有数据时禁止上拉加载
            setEnableLoadMore(adapter.data.isNotEmpty())
        }
        false -> {
            adapter.addData(newData)
            finishLoadMoreWithResult(newData, pageSize)
        }
    }
}

/**
 * 请求数据成功后，根据数据大小确定是否显示“没有数据”的脚布局
 */
fun SmartRefreshLayout.finishLoadMoreWithResult(result: List<Any>, pageSize: Int = 20) {
    if (result.size != pageSize) {
        finishLoadMoreWithNoMoreData()
    } else {
        finishLoadMore()
    }
}
```

在第一页数据没有填满页面时，只能下拉刷新不能上拉加载，对此，`SmartRefreshLayout`提供了解决方法：

```kotlin
        //数据没有填满一页时不能上拉加载更多，也可以在xml中设置
        rfMovie.setEnableLoadMoreWhenContentNotFull(false)
```

#### 3.1.3 请求失败

请求失败的处理比较简单，因为此时界面要么显示失败布局，要么保留原有数据。所以可以通过页面是否有旧数据决定是否禁止上拉加载和下拉刷新：

```kotlin
/**
 * 请求失败
 * 页面没有数据时应禁止上拉和下拉
 */
fun SmartRefreshLayout.refreshWhenFail(isEmptyPage: Boolean = true) {
    if (isEmptyPage) {
        setEnableRefresh(false)
        setEnableLoadMore(false)
    } else {
        finishRefresh(false)
        finishLoadMore(false)
    }
}

```

### 3.2 SmartRefreshLayout+BaseRecyclerViewAdapterHelper

#### 3.2.1 所用框架

- 上拉加载：[BaseRecyclerViewAdapterHelper](https://github.com/CymChad/BaseRecyclerViewAdapterHelper "BaseRecyclerViewAdapterHelper")
- 下拉刷新：[SmartRefreshLayout](https://github.com/scwang90/SmartRefreshLayout "SmartRefreshLayout")
- 多状态视图：[MultipleStatusView](https://github.com/qyxxjd/MultipleStatusView "MultipleStatusView")
- 适配器：[BaseRecyclerViewAdapterHelper](https://github.com/CymChad/BaseRecyclerViewAdapterHelper "BaseRecyclerViewAdapterHelper")

#### 3.2.1 请求成功

BaseRecyclerViewAdapterHelper需要根据是上拉加载还是下拉刷新来设置，沿用前面的思路，已`pageNo == 1`为依据判断。

```kotlin
/**
 * BaseQuickAdapter实现下拉加载更多功能
 * @param isGoneEndView:加载完所有的数据时是否去除“没有更多数据了”的底部布局
 */
fun <T : Any> BaseQuickAdapter<T, BaseViewHolder>.finishUpdateData(
    newData: List<T>,
    pageNo: Int,
    isGoneEndView: Boolean = false
) {
    if (pageNo == 1) {
        //下拉刷新，设置新数据
        setNewData(newData)
        //列表没有填满一页时不能上拉加载
        disableLoadMoreIfNotFullPage()
    } else {
        //上拉加载，添加新数据
        addData(newData)
        if (newData.isNotEmpty()) {
            //新数据不为空，本次加载完成
            loadMoreComplete()
        } else {
            //新数据为空，说明列表数据已经加载完毕，false表示会显示“没有更多数据了”的底部布局
            loadMoreEnd(isGoneEndView)
        }
    }
}
```
`BaseQuickAdapter`提供了方法`disableLoadMoreIfNotFullPage()`来禁止列表没有填满一页时也能上拉。

对于下拉刷新，我们仍然使用`SmartRefreshLayout`：

```kotlin
/**
 * SmartRefreshLayout+BaseQuickAdapter
 * SmartRefreshLayout负责下拉刷新，页面没有数据时默认不能下拉刷新，除非手动设置canEmptyRefresh为true
 * @param isEmptyPage: 页面是否有数据
 * @param canEmptyRefresh: 页面数据为空是否允许下拉刷新
 */
fun SmartRefreshLayout.finishRefreshWithAdapter(
    isEmptyPage: Boolean,
    canEmptyRefresh: Boolean = false
) {
    if (isEmptyPage && !canEmptyRefresh) {
        setEnableRefresh(false)
    } else {
        finishRefresh()
        //需要将下拉刷新打开
        setEnableRefresh(true)
    }
}

```

#### 3.2.2 请求失败

下拉请求失败时，**BRAH**对请求失败的处理很简单，只要一句：

```kotlin
    adapter.loadMoreFail()
```

上拉请求失败时，只需调用3.2.1中封装好的方法传入相应的参数即可：

```java
    rfMovie.finishRefreshWithAdapter(adapter.data.isEmpty(), canEmptyRefresh)
```

**BRAH**有一个我比较喜欢的地方就是加载失败之后会在列表底部添加一个“加载失败”的脚布局，这个脚布局默认可以点击继续下载，而且可以自定义。

#### 3.2.3 代码调用示例

```kotlin
        Handler().postDelayed({
            when (isNetworkConnect()) {
                //请求成功
                true -> {
                    shortToast("请求成功")
                    adapter.finishUpdateData(newData, pageNo)
                    rfMovie.finishRefreshWithAdapter(adapter.data.isEmpty(), canEmptyRefresh)
                    statusView.showSuccessView(adapter.data.isEmpty())
                    pageNo++
                }
                //请求失败
                false -> {
                    shortToast("网络异常")
                    rfMovie.finishRefreshWithAdapter(adapter.data.isEmpty())
                    statusView.showFailedView(adapter.data.isEmpty())
                    adapter.loadMoreFail()
                }
            }
        }, 1500)
```

## 4、总结

本文分析了多状态视图和上拉刷新下拉加载框架配合使用的流程和注意点，并给出了两种方案。这两种方案各有短长。 SmartRefreshLayout实现上拉加载和下拉刷新列表会具有默认的阻尼效果，滑动时会更加生动；而SmartRefreshLayout下拉刷新+**BRAH**上拉加载的代码更简洁，加载失败后的脚布局可以点击重新加载，也可以自定义自己喜欢的样式。

源码地址：[PullRefreshLoadMoreDemo](https://github.com/Lindroy/PullRefreshLoadMoreDemo "PullRefreshLoadMoreDemo")
