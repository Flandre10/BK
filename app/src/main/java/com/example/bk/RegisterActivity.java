// RegisterActivity.java
package com.example.bk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonRegisterConfirm;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 初始化Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // 初始化视图
        editTextEmail = findViewById(R.id.edit_text_register_email);
        editTextPassword = findViewById(R.id.edit_text_register_password);
        buttonRegisterConfirm = findViewById(R.id.button_register_confirm);

        // 初始化ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在注册...");
        progressDialog.setCancelable(false);

        buttonRegisterConfirm.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // 输入验证
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("请输入邮箱");
            editTextEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("请输入密码");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("密码长度应至少为6位");
            editTextPassword.requestFocus();
            return;
        }

        progressDialog.show();

        // 创建Firebase用户
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // 用户创建成功，创建Firestore文档
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String uid = firebaseUser.getUid();
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("email", email);
                            // 如果需要，可以添加其他字段，如头像URL等

                            db.collection("users").document(uid).set(userData)
                                    .addOnSuccessListener(aVoid -> {
                                        progressDialog.dismiss();
                                        Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_LONG).show();
                                        // 导航到主界面
                                        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        progressDialog.dismiss();
                                        Toast.makeText(RegisterActivity.this, "注册失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        // 如果需要，可以选择注销刚刚创建的Firebase用户
                                        mAuth.signOut();
                                    });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "用户信息错误", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        progressDialog.dismiss();
                        // 如果邮箱已存在，FirebaseAuth会自动抛出异常
                        Toast.makeText(RegisterActivity.this, "注册失败: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
