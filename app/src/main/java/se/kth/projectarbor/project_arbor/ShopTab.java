package se.kth.projectarbor.project_arbor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.*;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Fredrik Pihlqvist on 2017-04-28.
 */

public class ShopTab extends Fragment {

    private Button btnBuyWater;
    private Button btnBuySun;
    private TextView tvMoney;

    private int money;

    public enum StoreItem {
        WATER(10, 5),
        SUN(12, 7);

        private int amount;
        private int cost;

        private StoreItem(int amount, int cost) {
            this.amount = amount;
            this.cost = cost;
        }

        public int getAmount() {
            return amount;
        }

        public int getCost() {
            return cost;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_tab, container, false);

        // TODO: get money from different source
        this.money = 100;
        tvMoney = (TextView) view.findViewById(R.id.tvMoney);
        tvMoney.setText(""+this.money);

        btnBuyWater = (Button) view.findViewById(R.id.btnWater);
        btnBuyWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ARBOR", "buy");
                buy(StoreItem.WATER);
                tvMoney.setText(""+money);
            }
        });

        btnBuySun = (Button) view.findViewById(R.id.btnSun);
        btnBuySun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ARBOR", "BUY SUN");
                buy(StoreItem.SUN);
                tvMoney.setText(""+money);
            }
        });
        btnBuyWater.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                CharSequence text = " Inc Water by 5 Dec Money by 10!" ;
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(getActivity(),text ,duration);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
                return true;
            }
        });

        btnBuySun.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                CharSequence text = " Inc Sun by 7 Dec Money by 12!" ;
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(getActivity(),text ,duration);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
                return true;
            }
        });


        return view;
    }

    private boolean  withdrawMoney(int purchase) {
        if (money - purchase < 0) {
            return false;
        }
        else {
            this.money -= purchase;
            return true;
        }
    }

    public void buy(StoreItem item) {
        Log.d("ARBOR","BUY ");

        if(withdrawMoney(item.getCost())) {
            Intent intent = new Intent(getActivity(), MainService.class);
            intent.putExtra("MESSAGE_TYPE", MainService.MSG_PURCHASE);
            intent.putExtra("STORE_ITEM", item);
            getActivity().startService(intent);
        }
        else{
            CharSequence text = " Not Enough Money!" ;
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(getActivity(),text ,duration);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }
}
