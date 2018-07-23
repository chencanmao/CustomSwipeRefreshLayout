# CustomSwipeRefreshLayout
# 一个可以自定义下拉布局的下拉刷新控件
目前只支持4种下拉模式，分别是 “ContentAndRefreshDown”、“RefreshOverContent”、“ContentOverRefresh”、“RefreshHalfDown”   

下面是效果图：   
ContentAndRefreshDown模式   
![][ContentAndRefreshDown]  
RefreshOverContent 模式    
![][RefreshOverContent]   
ContentOverRefresh 模式   
![][ContentOverRefresh]     
RefreshHalfDown 模式  
![][RefreshHalfDown]   
# 使用
<com.ccm.view.CustomSwipeRefreshLayout  
        android:id="@+id/rfreshLayout"     
        android:layout_width="match_parent"   
        android:layout_height="match_parent"   
        app:refreshMode="RefreshHalfDown">     
        <RelativeLayout  
android:layout_width="match_parent"   
android:layout_height="100dp"    
android:gravity="center"    
android:background="#c0c0c0"    
app:freshLayout="refresh"  
>   
< TextView     
android:layout_width="wrap_content"    
android:layout_height="wrap_content"    
android:textColor="@android:color/white"    
android:textSize="24sp"    
android:text="这是刷新布局"
 />   
< /RelativeLayout >    
        <android.support.v7.widget.RecyclerView  
            android:id="@+id/list"  
            android:layout_width="match_parent"  
            android:layout_height="match_parent"   
            app:freshLayout="content">  
        </android.support.v7.widget.RecyclerView>  
  </com.ccm.view.CustomSwipeRefreshLayout>  
  

[ContentAndRefreshDown]:https://github.com/chencanmao/CustomSwipeRefreshLayout/raw/master/image/ContentAndRefreshDown.gif  
[RefreshOverContent]:https://github.com/chencanmao/CustomSwipeRefreshLayout/raw/master/image/RefreshOverContent.gif  
[ContentOverRefresh]:https://github.com/chencanmao/CustomSwipeRefreshLayout/raw/master/image/ContentOverRefresh.gif  
[RefreshHalfDown]:https://github.com/chencanmao/CustomSwipeRefreshLayout/raw/master/image/RefreshHalfDown.gif  
