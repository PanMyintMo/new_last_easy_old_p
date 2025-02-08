package com.gawilive.main.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.common.utils.ToastUtil;
import com.gawilive.main.R;
import com.gawilive.main.dialog.DiamondTransferFragment;
import com.gawilive.main.http.MainHttpUtil;

public class PinActivity extends AbsActivity {

    private ImageButton forwardArrowButton;
    private TextView createTextView, confirmPinTextView,sixPinNumber,retypePin;
    private LinearLayout pinContainer;
    private Button btnCreate;
    private EditText[] pinFields = new EditText[6];
    private String createdPin;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pin;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeViews();
        setupPinFieldAutoFocus();
        setClickListeners();
    }

    /**
     * Initialize all views and set default states.
     */
    private void initializeViews() {
        forwardArrowButton = findViewById(R.id.forwardArrow);
        createTextView = findViewById(R.id.createButton);
        confirmPinTextView = findViewById(R.id.confirmPinTextView);
        pinContainer = findViewById(R.id.pinContainer);
        btnCreate = findViewById(R.id.btnCreate);
        sixPinNumber=findViewById(R.id.sixPinNumber);
        retypePin=findViewById(R.id.retypePin);

        // Populate pinFields array with EditText children from pinContainer
        for (int i = 0; i < pinContainer.getChildCount(); i++) {
            View child = pinContainer.getChildAt(i);
            if (child instanceof EditText) {
                pinFields[i] = (EditText) child;
            }
        }

        // Set initial visibility
        confirmPinTextView.setVisibility(View.GONE);
        retypePin.setVisibility(View.GONE);
        btnCreate.setVisibility(View.GONE);
    }

    /**
     * Configure auto-focus behavior for PIN fields.
     */
    private void setupPinFieldAutoFocus() {
        for (int i = 0; i < pinFields.length; i++) {
            final int index = i;

            pinFields[index].setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == android.view.KeyEvent.ACTION_DOWN &&
                        keyCode == android.view.KeyEvent.KEYCODE_DEL &&
                        pinFields[index].getText().toString().isEmpty() &&
                        index > 0) {
                    pinFields[index - 1].requestFocus();
                }
                return false;
            });

            pinFields[index].addTextChangedListener(new android.text.TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!s.toString().isEmpty() && index < pinFields.length - 1) {
                        pinFields[index + 1].requestFocus();
                    }

                    updateButtonState();
                }

                @Override
                public void afterTextChanged(android.text.Editable s) {
                }
            });
        }
    }

    private void updateButtonState() {
        boolean isFilled=isPinFilled();
        boolean isMatching=createdPin != null && createdPin.equals(collectPin());

        if (isFilled && isMatching){
            btnCreate.setEnabled(true);
            btnCreate.setBackgroundTintList(getResources().getColorStateList(R.color.enable_color_transfer));

        }
        else{

            btnCreate.setEnabled(false);
            btnCreate.setBackgroundTintList(getResources().getColorStateList(R.color.disabled_color));

        }

    }

    /**
     * Attach click listeners to buttons.
     */
    private void setClickListeners() {
        forwardArrowButton.setOnClickListener(view -> handleForwardArrowClick());
        btnCreate.setOnClickListener(view -> handleCreateButtonClick());
    }

    /**
     * Handle forward arrow button click: Validate PIN and switch to confirmation view.
     */
    private void handleForwardArrowClick() {
        retypePin.setVisibility(View.VISIBLE);
        sixPinNumber.setVisibility(View.GONE);

        if (isPinFilled()) {
            createdPin = collectPin();
            confirmPinTextView.setVisibility(View.VISIBLE);
            createTextView.setVisibility(View.GONE);
            forwardArrowButton.setVisibility(View.GONE);
            btnCreate.setVisibility(View.VISIBLE);

            clearPinFields(); // Clear PIN fields for confirmation step
        } else {
            Toast.makeText(this, "Please enter all PIN digits", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handle create button click: Validate confirmation PIN and make API call.
     */
    private void handleCreateButtonClick() {
        if (isPinFilled()) {
            String confirmedPin = collectPin();

            if (createdPin.equals(confirmedPin)) {
                MainHttpUtil.createPin(createdPin, confirmedPin, new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info.length > 0) {
                            JSONObject obj = JSON.parseObject(info[0]);
                            String uid = obj.getString("id");
                            String token = obj.getString("token");
                            boolean success = obj.getBoolean("success");

                            if (uid != null && !uid.isEmpty() && token != null && !token.isEmpty()) {


                                // Save valid data in SharedPreferences
                                CommonAppConfig.getInstance().setPinNumber(uid, token, success);

                                // Notify the user
                                Toast.makeText(PinActivity.this, "PIN created successfully!", Toast.LENGTH_SHORT).show();

                                // Show the Diamond Transfer Dialog
                                DiamondTransferFragment dialog = new DiamondTransferFragment();
                                dialog.show(getSupportFragmentManager(), "DiamondTransferFragment");
                            } else {
                                CommonAppConfig.getInstance().clearPinNumber();

                                // Handle empty backend response
                                Toast.makeText(PinActivity.this, "Failed to create PIN. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Handle failure from the backend
                            ToastUtil.show(msg);
                        }
                    }


                });
            } else {
                Toast.makeText(PinActivity.this, "PINs do not match. Please try again.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(PinActivity.this, "Please fill in the PIN.", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Check if all PIN fields are filled.
     */
    private boolean isPinFilled() {
        for (EditText pinField : pinFields) {
            if (pinField.getText().toString().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Collect PIN digits from the fields.
     */
    private String collectPin() {
        StringBuilder pinBuilder = new StringBuilder();
        for (EditText pinField : pinFields) {
            pinBuilder.append(pinField.getText().toString());
        }
        return pinBuilder.toString();
    }

    /**
     * Clear all PIN fields.
     */
    private void clearPinFields() {
        for (EditText pinField : pinFields) {
            pinField.setText("");
        }
        pinFields[0].requestFocus(); // Focus on the first field
    }
}