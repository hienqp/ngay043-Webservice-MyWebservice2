package com.hienqp.mywebservice2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class UpdateSinhVienActivity extends AppCompatActivity {

    String urlUpdate = "http://192.168.1.8/androidwebservice/update.php";

    EditText edtHoTen, edtDiaChi, edtNamSinh;
    Button btnCapNhat, btnHuy;

    int id = 0; // biến id toàn cục dùng để hứng giá trị được gửi qua bởi Intent, không được để null, phải gán giá trị mặc định.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_sinh_vien);

        Intent intent = getIntent();
        SinhVien sinhVien = (SinhVien) intent.getSerializableExtra("dataSinhVien");

        AnhXa();

        id = sinhVien.getId();

        edtHoTen.setText(sinhVien.getHoTen());
        edtNamSinh.setText(String.valueOf(sinhVien.getNamSinh()));
        edtDiaChi.setText(sinhVien.getDiaChi());

        // bắt sự kiện click vào button CẬP NHẬT
        btnCapNhat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hoten = edtHoTen.getText().toString().trim();
                String namsinh = edtNamSinh.getText().toString().trim();
                String diachi = edtDiaChi.getText().toString().trim();

                // có thể dùng 1 trong 3 method equals, matches, length để kiểm tra 3 giá trị hoten, namsinh, diachi
                // nếu cả 3 giá trị đều không rỗng thì thực thi method CapNhatSinhVien
                if (hoten.equals("") || namsinh.matches("") || diachi.length()==0) {
                    Toast.makeText(UpdateSinhVienActivity.this, "Vui lòng nhập đủ thông tin !!!", Toast.LENGTH_SHORT).show();
                } else {
                    CapNhatSinhVien(urlUpdate);

                }
            }
        });

        // bắt sự kiện click vào button HỦY
        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void CapNhatSinhVien(String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.trim().equals("Success")) {
                            Toast.makeText(UpdateSinhVienActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                            
                            startActivity(new Intent(UpdateSinhVienActivity.this, MainActivity.class));
                        } else {
                            Toast.makeText(UpdateSinhVienActivity.this, "Lỗi cập nhật", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UpdateSinhVienActivity.this, "Xảy ra lỗi vui lòng thử lại", Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("idSV", String.valueOf(id));
                params.put("hotenSV", edtHoTen.getText().toString().trim());
                params.put("namsinhSV",edtNamSinh.getText().toString().trim());
                params.put("diachiSV",edtDiaChi.getText().toString().trim());

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void AnhXa() {
        btnCapNhat = findViewById(R.id.buttonCapNhat);
        btnHuy = findViewById(R.id.buttonHuyEdit);
        edtHoTen = findViewById(R.id.editTextTextHoTenEdit);
        edtNamSinh = findViewById(R.id.editTextNamSinhEdit);
        edtDiaChi = findViewById(R.id.editTextDiaChiEdit);
    }
}