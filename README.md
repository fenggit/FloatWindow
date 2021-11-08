# FloatWindow 安卓任意界面悬浮窗

![效果演示](/float_window.gif)


```java
minSdkVersion 19
targetSdkVersion 29
```

特性：
===

1.支持桌面和App内悬浮，需要权限

2.支持仅仅在App内悬浮，不需要权限

3.支持自定义悬浮窗样式

4.支持拖动，提供自动贴边等动画

5.支持权限申请操作


集成：
===

第 1 步、在工程的`build.gradle`中添加：

```groovy
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
Android Studio 2020.3.1 (Arctic Fox) 以後的版本需要添加至`settings.gradle`
```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```

第 2 步、在应用的`build.gradle`中添加：

```groovy
dependencies {
        implementation 'com.github.fenggit:FloatWindow:-a79eaae01f-1'
}
```

使用：
===

**0.声明权限**

```java
     <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
```


**1.基础使用**

```kotlin
    // 自定义的View
    var view = LayoutInflater.from(context).inflate(R.layout.float_view, null)
    
    var float = FloatWindow.With(context, view)
                .setAutoAlign(true)  //是否自动贴边
                .setModality(false)
                .setMoveAble(true)   // 是否可拖动
                .setStartLocation(0, (getScreenHeight(context) * 0.7).toInt())
                .create()

```


**2.TODO 指定界面显示**

```java
              .setFilter(true, A_Activity.class, C_Activity.class)

```
此方法表示 A_Activity、C_Activity 显示悬浮窗，其他界面隐藏。

```java
              .setFilter(false, B_Activity.class)
```

**1.知识点**
 - app内悬浮窗使用的是：`WindowManager.LayoutParams.TYPE_APPLICATION`
 
 - 桌面悬浮使用的是：`WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY` 和 `WindowManager.LayoutParams.TYPE_SYSTEM_ALERT`
 
为什么app内悬浮窗使用的没有使用`WindowManager.LayoutParams.TYPE_TOAST`？
因为高版本不支持
