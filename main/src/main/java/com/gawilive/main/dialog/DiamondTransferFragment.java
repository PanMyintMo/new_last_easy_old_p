package com.gawilive.main.dialog;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.common.utils.SpUtil;
import com.gawilive.common.utils.ToastUtil;
import com.gawilive.main.R;
import com.gawilive.main.activity.MainActivity;
import com.gawilive.main.http.MainHttpUtil;
import com.gawilive.main.interfaces.DiamondTransferListener;

public class DiamondTransferFragment extends DialogFragment {

    private Button btnTransfer;
    private EditText et_receiver_id;
    private EditText et_diamonds_amount;
    private EditText et_password;
    private ImageView iv_password_toggle;
    private DiamondTransferListener listener;
    private TextView diamondBalance;

    public DiamondTransferFragment() {
        // Required empty public constructor
    }

    public void setDiamondTransferListener(DiamondTransferListener listener) {
        this.listener = listener;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diamond_transfer, container, false);

        // Initialize views
        diamondBalance=view.findViewById(R.id.tv_diamonds_balance);
        et_receiver_id = view.findViewById(R.id.et_receiver_id);
        et_diamonds_amount = view.findViewById(R.id.et_diamonds_amount);
        et_password = view.findViewById(R.id.et_password);
        btnTransfer = view.findViewById(R.id.btnTransfer);
        iv_password_toggle=view.findViewById(R.id.iv_password_toggle);

        // Add TextWatchers
        TextWatcher inputWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Call checkInputFields on text change
                checkInputFields();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
            }
        };

        et_receiver_id.addTextChangedListener(inputWatcher);
        et_diamonds_amount.addTextChangedListener(inputWatcher);
        et_password.addTextChangedListener(inputWatcher);


        // Retrieve the stored diamond balance
        String storedBalance = SpUtil.getInstance().getDiamondBalance("coin_balance");

        // Set the retrieved balance to the TextView
        diamondBalance.setText(storedBalance != null ? storedBalance : "0");

        final boolean[] isPasswordVisible = {false};
        iv_password_toggle.setOnClickListener(v -> {
            if (isPasswordVisible[0]) {
                // Hide password
                et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                iv_password_toggle.setImageResource(R.mipmap.password); // Change icon to "eye-off"
            } else {
                // Show password
                et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                iv_password_toggle.setImageResource(R.mipmap.visible_password); // Change icon to "eye"
            }
            et_password.setSelection(et_password.getText().length()); // Keep cursor at the end
            isPasswordVisible[0] = !isPasswordVisible[0];
        });

        // Set up button click listener
        btnTransfer.setOnClickListener(v -> {
            // Your existing transfer logic
            String receiverIdStr = et_receiver_id.getText().toString().trim();
            String diamondsAmountStr = et_diamonds_amount.getText().toString().trim();
            String password = et_password.getText().toString().trim();



            if (receiverIdStr.isEmpty() || diamondsAmountStr.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "All fields are required!", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int receiverId = Integer.parseInt(receiverIdStr);
                int diamondsAmount = Integer.parseInt(diamondsAmountStr);

                MainHttpUtil.diamondTransfer(receiverId, diamondsAmount, password, new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        //Toast.makeText(getContext(), "Transfer successful!", Toast.LENGTH_SHORT).show();
                        dismiss();

                        if (code == 0 && info.length > 0) {
                            JSONObject obj = JSON.parseObject(info[0]);
                            String uid = obj.getString("id");
                            String token = obj.getString("token");
                            boolean success = obj.getBoolean("success");

                            CommonAppConfig.getInstance().setPinNumber(uid, token, success);
                        } else {

                            if (listener != null) {

                                listener.onTransferComplete(true); // Notify success
                            }
                            ToastUtil.show(msg);
                        }

                        dismiss();


                    }

                    @Override
                    public void onError() {
                        Toast.makeText(getContext(), "Transfer failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Please enter valid numbers for Receiver ID and Amount.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void checkInputFields() {
        if (!et_receiver_id.getText().toString().isEmpty() &&
                !et_diamonds_amount.getText().toString().isEmpty() &&
                !et_password.getText().toString().isEmpty()) {

            btnTransfer.setEnabled(true);
            btnTransfer.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.enabled_color));
        } else {
            btnTransfer.setEnabled(false);
            btnTransfer.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.disabled_color));
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            Window window = getDialog().getWindow();
            setWindowAttributes(window);
        }
    }

    /**
     * Configure window attributes such as size, position, and cancelable behavior.
     */
    protected void setWindowAttributes(Window window) {
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);

        // Optional: Disable canceling on outside touch
        getDialog().setCancelable(true);
    }


    // Method to be called when the coin balance is updated
    private void updateCoinBalance(String newBalance) {
        Bundle result = new Bundle();
        result.putString("coin_balance", newBalance);
        // Use the parent FragmentManager to set the result
        getParentFragmentManager().setFragmentResult("coinBalanceKey", result);
    }


    /**
     * Static method to create a new instance of the dialog fragment.
     */
    public static DiamondTransferFragment newInstance() {
        return new DiamondTransferFragment();
    }

}