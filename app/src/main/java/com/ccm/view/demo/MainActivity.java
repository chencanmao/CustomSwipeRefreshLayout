package com.ccm.view.demo;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ccm.view.CustomSwipeRefreshLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private CustomSwipeRefreshLayout mCustomSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }
    private void init(){
        mCustomSwipeRefreshLayout = findViewById(R.id.rfreshLayout);
        mRecyclerView = findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        mRecyclerView.setAdapter(new Adapter());
        mCustomSwipeRefreshLayout.setListener(new CustomSwipeRefreshLayout.RefreshListener() {
            @Override
            public void onFresh(boolean b) {
                if(b){
                    // 1秒后刷新完成
                    getWindow().getDecorView().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mCustomSwipeRefreshLayout.setRefreshing(false);
                        }
                    },1000);
                }
            }

            @Override
            public void progress(int p) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = (Integer) v.getTag();
        Toast.makeText(getApplicationContext(),String.format("item %d 被点击了",i),Toast.LENGTH_SHORT).show();
    }


    class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getBaseContext());
            return new ViewHolder(layoutInflater.inflate(R.layout.item,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.itemView.setTag(position);
            holder.itemView.setOnClickListener(MainActivity.this);
        }

        @Override
        public int getItemCount() {
            return 20;
        }

        class ViewHolder extends RecyclerView.ViewHolder{

            public ViewHolder(View itemView) {
                super(itemView);

            }
        }
    }
}
