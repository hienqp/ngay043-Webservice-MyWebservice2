package com.hienqp.mywebservice2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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

public class AddSinhVienActivity extends AppCompatActivity {

    EditText edtHoTen, edtNamSinh, edtDiaChi;
    Button btnThem, btnHuy;
    String urlInsert = "http://192.168.1.8/androidwebservice/insert.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sinh_vien);

        AnhXa();

        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hoten = edtHoTen.getText().toString().trim();
                String namsinh = edtNamSinh.getText().toString().trim();
                String diachi = edtDiaChi.getText().toString().trim();

                if (hoten.isEmpty() || namsinh.isEmpty() || diachi.isEmpty()) {
                    Toast.makeText(AddSinhVienActivity.this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                } else {
                    ThemSinhVien(urlInsert);
                }
            }
        });

        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void ThemSinhVien(String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // kiểm tra response kết quả trả về
                        // khi file insert.php được thực hiện thành công sẽ trả về String "Success"
                        // vì vậy nếu Server trả về "Success" ta Toast cho user biết được kết quả thành công
                        if (response.trim().equals("Success")) {
                            Toast.makeText(AddSinhVienActivity.this, "Thêm Thành Công", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(AddSinhVienActivity.this, MainActivity.class));
                        } else {
                            Toast.makeText(AddSinhVienActivity.this, "Lỗi thêm !!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AddSinhVienActivity.this, "Xảy Ra Lỗi", Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            // StringRequest đã sử dụng method POST và url dẫn đến file php xử lý lệnh insert trên server
            // method getParams dưới đây sẽ trả về 1 Map<K,V> params và được POST lên
            // với mỗi method POST trên server sẽ nhận đúng giá trị Map tương ứng với Key cho trước
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("hotenSV", edtHoTen.getText().toString().trim()); // _POST[hotenSV] sẽ nhận Value này
                params.put("namsinhSV", edtNamSinh.getText().toString().trim()); // _POST[namsinhSV] sẽ nhận Value này
                params.put("diachiSV", edtDiaChi.getText().toString().trim()); // _POST[diachiSV] sẽ nhận Value này

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void AnhXa() {
        edtHoTen = findViewById(R.id.editTextHoTen);
        edtNamSinh = findViewById(R.id.editTextNamSinh);
        edtDiaChi = findViewById(R.id.editTextDiaChi);
        btnThem = findViewById(R.id.buttonThem);
        btnHuy = findViewById(R.id.buttonHuyThem);

    }
}