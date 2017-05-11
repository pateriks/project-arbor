package se.kth.projectarbor.project_arbor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.*;
import android.text.Layout;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Fredrik Pihlqvist, Johan Andersson, Pethrus Gärdborn on 2017-04-28.
 */

public class ShopTab extends Fragment {

    private int money;
    private final int SUN_COLOR = 0xFFF2941F;
    private final int WATER_COLOR = 0xFF00B9D3;
    private final int PURCHASE_TEXT_SIZE = 54;
    private final int NO_MONEY_TEXT_SIZE = 32;
    private final int PURCHASE_BUTTON_TINT = 0x1F00FF00;
    private final int NO_MONEY_BUTTON_TINT = 0x1FFF0000;

    //private boolean animReady;


    private ImageView btnWaterSmall;
    private ImageView btnWaterMedium;
    private ImageView btnWaterLarge;
    private ImageView btnSunSmall;
    private ImageView btnSunMedium;
    private ImageView btnSunLarge;
    private TextView textMoney;

    // To store money and make it available to other parts of app
    private SharedPreferences sharedPreferences;

    private RelativeLayout.LayoutParams layoutParams;
    private TextView textReceipt;

    // Add more items as needed
    public enum StoreItem {
        WATER_SMALL(75, 10),
        WATER_MEDIUM(200, 50),
        WATER_LARGE(500, 100),
        SUN_SMALL(75, 10),
        SUN_MEDIUM(200, 50),
        SUN_LARGE(500, 100);

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


    private class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Receives messages from pedometer as user is walking to increase money and update display
            if (intent.getAction().equals(Pedometer.STORE_BROADCAST)) {
                money += intent.getIntExtra("MONEY", 0);
                textMoney.setText("Curreny: " + money);
                sharedPreferences.edit().putInt("STORE_MONEY", money).apply();
            // Receives messages from MainService to update display weather data
            } else if (intent.getAction().equals(MainService.TREE_DATA)) {
                Bundle extras = intent.getExtras();
                // tvShopSun.setText("SUN: " + extras.getInt("SUN"));
                // tvShopWater.setText("WATER: " + extras.getInt("WATER"));
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_tab, container, false);

        // These may be used when doing animation programmatically
        //Display display = getActivity().getWindowManager().getDefaultDisplay();
        //Point size = new Point();
        //display.getSize(size);
        //layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
        //       ViewGroup.LayoutParams.WRAP_CONTENT);
        //textReceipt = new TextView(getActivity());
        textReceipt = (TextView) view.findViewById(R.id.text_receipt);
        textReceipt.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        textReceipt.setTextSize(PURCHASE_TEXT_SIZE);
        //textReceipt.setLayoutParams(layoutParams);
        textReceipt.setVisibility(View.INVISIBLE);

        final Animation animationAppear = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.shop_receipt_appear);
        final Animation animationWait = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.shop_receipt_wait);
        final Animation animationDisappear = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.shop_receipt_disappear);

        // The following three method calls, make possible to execute the three animations (see xml)
        // in sequence.

        // This listener makes the feedback text visible when the user buys an item
        // and translates the text to the center
        animationAppear.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                    textReceipt.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                textReceipt.startAnimation(animationWait);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        // This listener makes the feedback text stand still for a short while
        animationWait.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                textReceipt.startAnimation(animationDisappear);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        // This listener makes the feedback text go off screen
        animationDisappear.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                textReceipt.setVisibility(View.INVISIBLE);
                textReceipt.setTextSize(PURCHASE_TEXT_SIZE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //ViewGroup layout = (ViewGroup) view.findViewById(R.id.shop_top_layout);
        //layout.addView(textReceipt);

        // Setup a filter for money
        IntentFilter filter = new IntentFilter();
        filter.addAction(Pedometer.STORE_BROADCAST);
        filter.addAction(MainService.TREE_DATA);
        getActivity().registerReceiver(this.new Receiver(), filter);

        sharedPreferences = getActivity().getSharedPreferences("STORE_MONEY", Context.MODE_PRIVATE);

        // If money has been stored earlier, read from sharedPreferences
        if (sharedPreferences.contains("STORE_MONEY")) {
            money = sharedPreferences.getInt("STORE_MONEY", 0);
        // Else, set initial money value
        } else {
            money = 2000;
            sharedPreferences.edit().putInt("STORE_MONEY", money).apply();
        }

        textMoney = (TextView) view.findViewById(R.id.text_money);
        textMoney.setText(this.money + "gp");

        // Buttons that give feedback when pressed
        btnWaterSmall = (ImageView) view.findViewById(R.id.box_water_small);
        btnWaterSmall.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(purchaseItem(StoreItem.WATER_SMALL, WATER_COLOR, animationAppear))
                        btnWaterSmall.setColorFilter(PURCHASE_BUTTON_TINT);
                    else
                        btnWaterSmall.setColorFilter(NO_MONEY_BUTTON_TINT);
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btnWaterSmall.clearColorFilter();
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_CANCEL){
                    btnWaterSmall.clearColorFilter();
                    return true;
                }

                return false;
            }
        });

        btnWaterMedium = (ImageView) view.findViewById(R.id.box_water_medium);
        btnWaterMedium.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(purchaseItem(StoreItem.WATER_MEDIUM, WATER_COLOR, animationAppear))
                        btnWaterMedium.setColorFilter(PURCHASE_BUTTON_TINT);
                    else
                        btnWaterMedium.setColorFilter(NO_MONEY_BUTTON_TINT);
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btnWaterMedium.clearColorFilter();
                    return true;
                } else if(event.getAction() == MotionEvent.ACTION_CANCEL){
                    btnWaterMedium.clearColorFilter();
                    return true;
                }

                return false;
            }
        });

        btnWaterLarge = (ImageView) view.findViewById(R.id.box_water_large);
        btnWaterLarge.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(purchaseItem(StoreItem.WATER_LARGE, WATER_COLOR, animationAppear))
                        btnWaterLarge.setColorFilter(PURCHASE_BUTTON_TINT);
                    else
                        btnWaterLarge.setColorFilter(NO_MONEY_BUTTON_TINT);
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btnWaterLarge.clearColorFilter();
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_CANCEL){
                    btnWaterLarge.clearColorFilter();
                    return true;
                }

                return false;
            }
        });

        btnSunSmall = (ImageView) view.findViewById(R.id.box_sun_small);
        btnSunSmall.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(purchaseItem(StoreItem.SUN_SMALL, SUN_COLOR, animationAppear))
                        btnSunSmall.setColorFilter(PURCHASE_BUTTON_TINT);
                    else
                        btnSunSmall.setColorFilter(NO_MONEY_BUTTON_TINT);
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btnSunSmall.clearColorFilter();
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_CANCEL){
                    btnSunSmall.clearColorFilter();
                    return true;
                }

                return false;
            }
        });

        btnSunMedium = (ImageView) view.findViewById(R.id.box_sun_medium);
        btnSunMedium.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(purchaseItem(StoreItem.SUN_MEDIUM, SUN_COLOR, animationAppear))
                        btnSunMedium.setColorFilter(PURCHASE_BUTTON_TINT);
                    else
                        btnSunMedium.setColorFilter(NO_MONEY_BUTTON_TINT);
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btnSunMedium.clearColorFilter();
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_CANCEL){
                    btnSunMedium.clearColorFilter();
                    return true;
                }

                return false;
            }
        });

        btnSunLarge = (ImageView) view.findViewById(R.id.box_sun_large);
        btnSunLarge.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(purchaseItem(StoreItem.SUN_LARGE, SUN_COLOR, animationAppear))
                        btnSunLarge.setColorFilter(PURCHASE_BUTTON_TINT);
                    else
                        btnSunLarge.setColorFilter(NO_MONEY_BUTTON_TINT);
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btnSunLarge.clearColorFilter();
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_CANCEL){
                    btnSunLarge.clearColorFilter();
                    return true;
                }

                return false;
            }
        });

        return view;
    }

    // Buy item and start animation
    private boolean purchaseItem(StoreItem item, final int color, Animation anim) {
        Log.d("ARBOR", "purchasing");
        textReceipt.setTextColor(ColorStateList.valueOf(color));
            if(buy(item)) {
                textMoney.setText(ShopTab.this.money + "gp");
                textReceipt.startAnimation(anim);
                return true;
            }
            else {
                textReceipt.startAnimation(anim);
                return false;
            }
    }

    // Returns true if possible to make purchase
    private boolean withdrawMoney(int purchase) {
        if (money - purchase < 0) {
            return false;
        }
        else {
            this.money -= purchase;
            sharedPreferences.edit().putInt("STORE_MONEY", money).apply();
            return true;
        }
    }

    // Broadcast your purchase to the main service
    private boolean buy(StoreItem item) {
        Log.d("ARBOR","BUY ");

        // If enough money to buy item
        if(withdrawMoney(item.getCost())) {
            Intent intent = new Intent(getActivity(), MainService.class);
            intent.putExtra("MESSAGE_TYPE", MainService.MSG_PURCHASE);
            intent.putExtra("STORE_ITEM", item);
            getActivity().startService(intent);
            textReceipt.setText("+" + item.amount);
            return true;
        } else {
            textReceipt.setText("Not enough pollen");
            textReceipt.setTextSize(NO_MONEY_TEXT_SIZE);
            return false;
        }
    }
}

