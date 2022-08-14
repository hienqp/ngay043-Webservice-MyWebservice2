___
VIẾT FILE PHP KẾT NỐI DATABASE, GET DATA, ĐỔ DATA RA TRÌNH DUYỆT DƯỚI DẠNG JSON

  - truy cập đến folder ``C:\xampp\htdocs\androidwebservice``
  - tạo file ``getdata.php``
  - trong ``getdata.php`` viết code mẫu kiểm tra XAMPP hoạt động ổn định hay không

  ```php
      <?php
        echo "GET DATA";
      ?>
  ```

  - từ trình duyệt truy cập đến ``http://localhost/androidwebservice/getdata.php``, nếu hiển thị ``GET DATA`` thì XAMPP đang hoạt động bình thường
  - trong file ``getdata.php`` ta tiến hành code chương trình kết nối đến database ``sinhvien``, truy vấn đến table ``student`` lấy data đưa vào mảng, sau đó chuyển sang dạng JSON ``echo`` ra trình duyệt để kiểm tra

  - getdata.php
  
  ```php
  <?php

// KẾT NỐI ĐẾN DATABASE
  // từ ver 5.6 ta dùng mysqli_connect và truyền vào 4 tham số dạng chuỗi:
    // "tên server lưu trữ database" (localhoast)
    // "tên user đăng nhập vào database" (mặc định của XAMPP là root)
    // "mật khẩu đăng nhập của user vào database" (mặc định của XAMPP là rỗng)
    // "tên của database" (database ta đang cần là sinhvien)
  $connect = mysqli_connect("localhost","root","","sinhvien"); // CỔNG KẾT NỐI

  // truy vấn đến cổng kết nối đã thiết lập, bước này chưa truy vấn nên ta chỉ thực hiện thiết lập kiểu định dạng utf8 để trả về tiếng việt có dấu
  mysqli_query($connect, "SET NAMES 'utf8'");

  $query = "SELECT * FROM student";
  $data = mysqli_query($connect, $query);


  // 1. Tạo class SinhVien
  class SinhVien{
    public $ID;
    public $HoTen;
    public $NamSinh;
    public $DiaChi;

    public function __construct($id, $hoten, $namsinh, $diachi){
      $this->ID = $id;
      $this->HoTen = $hoten;
      $this->NamSinh = $namsinh;
      $this->DiaChi = $diachi;
    }
  }
  // 2. Tạo mảng
  $mangSV = array();

  // 3. Thêm phần tử vào mảng
  while ($row = mysqli_fetch_assoc($data)) {
    array_push($mangSV, new SinhVien($row['id'], $row['hoten'], $row['namsinh'], $row['diachi']));
  }

  // 4. Chuyển định dạng của mảng -> JSON
  echo json_encode($mangSV);

?>
  ```
___
TẠO PROJECT ĐỌC DATABASE DẠNG JSON ĐỔ RA LISTVIEW

- xin quyền INTERNET trong AndroidManifest.xml
- từ API 28 trở lên, trong ``AndroidManifest.xml`` ta cần thêm ``android:usesCleartextTraffic="true"`` vào tag ``application``.
- thêm thư viện Volley vào ``Build.gradle`` Module

- code mẫu project

```java
package com.hienqp.mywebservice2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

public class MainActivity extends AppCompatActivity {

    String urlGetdata = "http://192.168.1.8/androidwebservice/getdata.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GetData(urlGetdata);
    }

    private void GetData(String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonArrayRequest);
    }
}
```

- tạo class __SinhVien__
  - mở thẻ Project ở kiểu hiển thị Android
  - trong Package chứa MainActivity tạo file java __SinhVien__ với cấu trúc như dưới đây

```java
package com.hienqp.mywebservice2;

public class SinhVien {
    private int Id;
    private String HoTen;
    private int NamSinh;
    private String DiaChi;

    public SinhVien(int id, String hoTen, int namSinh, String diaChi) {
        Id = id;
        HoTen = hoTen;
        NamSinh = namSinh;
        DiaChi = diaChi;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getHoTen() {
        return HoTen;
    }

    public void setHoTen(String hoTen) {
        HoTen = hoTen;
    }

    public int getNamSinh() {
        return NamSinh;
    }

    public void setNamSinh(int namSinh) {
        NamSinh = namSinh;
    }

    public String getDiaChi() {
        return DiaChi;
    }

    public void setDiaChi(String diaChi) {
        DiaChi = diaChi;
    }
}
```

- thiết kế layout hiển thị mỗi dòng sinh viên
  - truy cập đến package res/layout/
  - chuột phải Package res/layout/ -> new -> Layout Resource File
  - đặt tên layout __dong_sinh_vien__

```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="10dp">

    <TextView
        android:layout_marginTop="10dp"
        android:id="@+id/textviewHotenCustom"
        android:textStyle="bold"
        android:textColor="@color/purple_700"
        android:textSize="24sp"
        android:text="Họ tên Sinh viên"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"/>

    <TextView
        android:id="@+id/textviewNamsinhCustom"
        android:textColor="#4DB110"
        android:layout_marginTop="10dp"
        android:layout_below="@id/textviewHotenCustom"
        android:textSize="20sp"
        android:text="Năm sinh: 1999"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"/>

    <TextView
        android:id="@+id/textviewDiachiCustom"
        android:textColor="#212321"
        android:layout_marginTop="10dp"
        android:layout_below="@id/textviewHotenCustom"
        android:layout_alignParentRight="true"
        android:textSize="20sp"
        android:text="Cà Mau"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"/>

    <ImageView
        android:id="@+id/imageviewEdit"
        android:layout_marginTop="17dp"
        android:layout_alignParentEnd="true"
        android:src="@android:drawable/ic_menu_edit"
        android:layout_width="25dp"
        android:layout_height="25dp"/>

    <ImageView
        android:id="@+id/imageviewDelete"
        android:layout_width="25dp"
        android:layout_height="25dp"
        android:layout_marginTop="17dp"
        android:layout_marginEnd="10dp"
        android:layout_toStartOf="@id/imageviewEdit"
        android:src="@android:drawable/ic_menu_delete" />

</RelativeLayout>
```

- Tạo class Adapter __SinhVienAdapter__
  - click chuột phải vào package của project -> new -> Java Class -> Name: SinhVienAdapter
  - __SinhVienAdapter__ sẽ __extends BaseAdapter__
  - __implements__ 4 method của __BaseAdapter__
    - getCount
    - getItem
    - getItemId
    - getView
  - khai báo 3 thuộc tính __private__
    - context kiểu Context
    - Dòng layout kiểu int
    - List chứa các đối tượng SinhVien
  - khai báo Constructor gồm 3 thuộc tính trên
  - thêm __private class ViewHolder__ vào __SinhVienAdapter__ và khai báo các thuộc tính vủa __ViewHolder__ chính là các View của dòng layout
  - tinh chỉnh các method đã implements
    - __getCount__ return size của List các SinhVien
    - __getView__ 

- __SinhVienAdapter__
```java
package com.hienqp.mywebservice2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class SinhVienAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private List<SinhVien> sinhVienList;

    public SinhVienAdapter(Context context, int layout, List<SinhVien> sinhVienList) {
        this.context = context;
        this.layout = layout;
        this.sinhVienList = sinhVienList;
    }

    @Override
    public int getCount() {
        return sinhVienList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder {
        TextView txtHoTen, txtNamSinh, txtDiaChi;
        ImageView imgDelete, imgEdit;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);

            holder.txtHoTen     = convertView.findViewById(R.id.textviewHotenCustom);
            holder.txtNamSinh   = convertView.findViewById(R.id.textviewNamsinhCustom);
            holder.txtDiaChi    = convertView.findViewById(R.id.textviewDiachiCustom);
            holder.imgDelete    = convertView.findViewById(R.id.imageviewDelete);
            holder.imgEdit      = convertView.findViewById(R.id.imageviewEdit);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SinhVien sinhVien = sinhVienList.get(position);

        holder.txtHoTen.setText(sinhVien.getHoTen());
        holder.txtNamSinh.setText("Năm sinh: " + sinhVien.getNamSinh());
        holder.txtDiaChi.setText(sinhVien.getDiaChi());

        return convertView;
    }
}
```

- chỉnh sửa layout __activity_main.xml__

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <ListView
        android:id="@+id/listviewSinhVien"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"> </ListView>
</LinearLayout>
```

- chỉnh sửa __MainActivity__

```java
package com.hienqp.mywebservice2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    String urlGetdata = "http://192.168.1.8/androidwebservice/getdata.php";

    ListView lvSinhvien;
    ArrayList<SinhVien> arraySinhVien;
    SinhVienAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvSinhvien = findViewById(R.id.listviewSinhVien);
        arraySinhVien = new ArrayList<>();

        adapter = new SinhVienAdapter(this, R.layout.dong_sinh_vien, arraySinhVien);

        lvSinhvien.setAdapter(adapter);

        GetData(urlGetdata);
    }

    private void GetData(String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                arraySinhVien.add(new SinhVien(
                                        jsonObject.getInt("ID"),
                                        jsonObject.getString("HoTen"),
                                        jsonObject.getInt("NamSinh"),
                                        jsonObject.getString("DiaChi")
                                ));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonArrayRequest);
    }
}
```

- bật __Apache__ và __MySQL__ trong __XAMPP__
- run app

___

VIẾT FILE PHP INSERT DATA TỪ APP LÊN WEBSERVICE

- vào folder __C:\xampp\htdocs\androidwebservice__ tạo 1 file __insert.php__ dùng để insert data và database trên webserive.
- do thao tác vào database nên trong file __insert.php__ phải có câu lệnh kết nối và truy cập vào database

```php
<?php 
  $connect = mysqli_connect("localhost","root","","sinhvien");
  mysqli_query($connect, "SET NAMES 'utf8'");
?>
```

- để tránh việc lặp lại đoạn code này ở những file php update hoặc delete data, ta tạo 1 file php riêng chỉ dùng để connect đến database
- tạo file __dbCon.php__

```php
<?php 
// KẾT NỐI ĐẾN DATABASE
  // từ ver 5.6 ta dùng mysqli_connect và truyền vào 4 tham số dạng chuỗi:
    // "tên server lưu trữ database" (localhoast)
    // "tên user đăng nhập vào database" (mặc định của XAMPP là root)
    // "mật khẩu đăng nhập của user vào database" (mặc định của XAMPP là rỗng)
    // "tên của database" (database ta đang cần là sinhvien)
  $connect = mysqli_connect("localhost","root","","sinhvien"); // CỔNG KẾT NỐI

  // truy vấn đến cổng kết nối đã thiết lập, bước này chưa truy vấn nên ta chỉ thực hiện thiết lập kiểu định dạng utf8 để trả về tiếng việt có dấu
  mysqli_query($connect, "SET NAMES 'utf8'");
?>
```

- ở __insert.php__ để gọi 2 câu lệnh kết nối database từ __dbCon.php__ ta chỉ việc __require "dbCon.php";__ (do insert.php và dbCon.php đang cùng folder - nghĩa là đang ngang hàng với nhau), làm tương tự với __getdata.php__.

```php
<?php
require "dbCon.php";
?>
```

- khai báo 3 biến __$hoten, $namsinh, $diachi__ thủ công để test file __insert.php__

```php
<?php
$hoten = "Tiến Minh";
$namsinh = "2000";
$diachi = "Hồ Chí Minh";
?>
```

- khai báo câu lệnh truy vấn insert data và database

```php
<?php
$query = "INSERT INTO student VALUE('$hoten', '$namsinh', '$diachi')";
?>
```

- ta thử dùng câu lệnh kiểm tra lệnh truy vấn insert có thực hiện được hay không
```php
<?php
// nếu kết connect và query thành công trả về true nghĩa là success
// do đã require "dbCon.php" nên có thể gọi $connect bình thường từ insert.php
if(mysqli_query($connect, $query)) {
  echo "Success";
} else {
  echo "Error";
}
?>
```

- sau khi hoàn thành các bước trên ta kiểm tra từ trình duyệt
  - __http://localhost/androidwebservice/insert.php__ hiển thị __Success__ là ok
  - hoặc trên table của database sẽ cập nhật thêm dòng data mới là ok

- sau khi test insert data thủ công, ta sử dụng phương thức POST dùng để gửi data thay cho phương pháp thủ công
- trong PHP hoặc lập trình web nói chung, để gửi và nhận dữ liệu ta sử dụng 1 trong 2 phương thức POST hoặc GET
- ở đây ta sử dụng phương thức POST vì POST an toàn, nhiều ưu điểm hơn

```php
<?php
  $hoten = $_POST['hotenSV'];
  $namsinh = $_POST['namsinhSV'];
  $diachi = $_POST['diachiSV'];
?>
```

- như vậy ta có file __insert.php__ hoàn chỉnh

```php
<?php
  require "dbCon.php";

  $hoten = $_POST['hotenSV'];
  $namsinh = $_POST['namsinhSV'];
  $diachi = $_POST['diachiSV'];

  $query = "INSERT INTO student VALUE(null, '$hoten', '$namsinh', '$diachi')";

  if (mysqli_query($connect, $query)) {
    echo "Success";
  } else {
    echo "Error";
  }
?>
```

- THIẾT KẾ GIAO DIỆN MENU INSERT DATA
- click phải __res/ -> new -> Directory__, nhập tên của Directory là __menu__
- click phải __res/menu/ -> new -> Menu Resource File__, nhập tên layout menu là __add_student__
- thiết kế giao diện của menu __add_student__ như sau

```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">
    <item 
        android:title="Thêm Sinh Viên"
        android:id="@+id/menuAddStudent"
        android:icon="@android:drawable/ic_input_add"
        app:showAsAction="always"/>
</menu>
```

- định nghĩa 1 Activity là màn hình sẽ xuất hiện khi user click vào button add trên menu
- click phải package chứa source file java của project __-> new -> Activity -> Empty Activity__, nhập tên Activity là __AddSinhVienActivity__
- lúc này quay trở lại __MainActivity__ ta sẽ override 2 method dùng để __Create Option__ và bắt sự kiện __Select Item__ trên menu

- __MainActivity__

```java
package com.hienqp.mywebservice2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    String urlGetdata = "http://192.168.1.8/androidwebservice/getdata.php";

    ListView lvSinhvien;
    ArrayList<SinhVien> arraySinhVien;
    SinhVienAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvSinhvien = findViewById(R.id.listviewSinhVien);
        arraySinhVien = new ArrayList<>();

        adapter = new SinhVienAdapter(this, R.layout.dong_sinh_vien, arraySinhVien);

        lvSinhvien.setAdapter(adapter);

        GetData(urlGetdata);
    }

    private void GetData(String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                arraySinhVien.add(new SinhVien(
                                        jsonObject.getInt("ID"),
                                        jsonObject.getString("HoTen"),
                                        jsonObject.getInt("NamSinh"),
                                        jsonObject.getString("DiaChi")
                                ));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonArrayRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_student, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuAddStudent) {
            startActivity(new Intent(MainActivity.this, AddSinhVienActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
```

___

INSERT TỪ APP LÊN SERVER

- sau khi đã bắt sự kiện click vào button add sinh viên trên menu, ta tiến hành thiết kế activity thêm sinh viên
- thiết kế UI của __AddSinhVienActivity__
  - 1 TextView
  - 3 EditText
  - 1 Button

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:background="@color/teal_200"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".AddSinhVienActivity">

    <TextView
        android:id="@+id/textView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="36dp"
        android:text="Thêm Sinh Viên"
        android:textColor="#ED0B0B"
        android:textSize="34sp"
        android:textStyle="bold"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.498"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <EditText
        android:id="@+id/editTextHoTen"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="32dp"
        android:layout_marginTop="50dp"
        android:layout_marginEnd="32dp"
        android:ems="10"
        android:hint="Nhập họ tên"
        android:inputType="textPersonName"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.497"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/textView" />

    <EditText
        android:id="@+id/editTextNamSinh"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="32dp"
        android:layout_marginTop="32dp"
        android:layout_marginEnd="32dp"
        android:ems="10"
        android:hint="Nhập năm sinh"
        android:inputType="textPersonName"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.0"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/editTextHoTen" />

    <EditText
        android:id="@+id/editTextDiaChi"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="32dp"
        android:layout_marginTop="32dp"
        android:layout_marginEnd="32dp"
        android:ems="10"
        android:hint="Nhập địa chỉ"
        android:inputType="textPersonName"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.0"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/editTextNamSinh" />

    <Button
        android:id="@+id/buttonThem"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginStart="30dp"
        android:layout_marginTop="40dp"
        android:text="Thêm"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/editTextDiaChi" />

    <Button
        android:id="@+id/buttonHuyThem"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="40dp"
        android:layout_marginEnd="30dp"
        android:text="Hủy"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/editTextDiaChi" />
    
</androidx.constraintlayout.widget.ConstraintLayout>
```

- sau khi đã thiết kế UI cho activity __AddSinhVienActivity__ ta tiến hành thiết lập logic để activity này hoạt động như mong muốn.
  - khai báo ánh xạ các View
    - 1 TextView
    - 3 EditText
    - 1 Button
  - đối với button Hủy, bắt sự kiện khi click vào sẽ __finish()__ activity thêm sinh viên
  - đối với button Thêm, bắt sự kiện khi click vào sẽ gọi đến phương thức __ThemSinhVien(String url)__
    - trong method __ThemSinhVien(String url)__ ta sử dụng __API Volley__ để __POST__ data lên server
    - để nhận data từ server về app, ta sử dụng __JSONObject__ để đọc dữ liệu từ response trả về
    - nhưng sau khi có được __RequestQueue__ và __StringRequest__ ta chỉ đơn giản là nhận phản hồi connect tới server
    - để có thể __POST__ data lên server thì trước khi kết thúc constructor khởi tạo StringRequest với __;__ ta override method __getParams();__ trong __{}__
    - __getParams__ sẽ trả về 1 __Map__ chứa __Key__ và __Value__
    - lưu ý: các giá trị __Key__ phải trùng với giá trị của method __POST__ trên server
    - sau khi __POST__ data (Map params) lên server (url insert.php), ta sẽ nhận được response, tùy vào response trả về mà ta xử lý với response thành công và response error

```java
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
```

___

UPDATE DATA TỪ APP LÊN SERVER

- làm chức năng click vào icon edit, data sẽ hiển thị lên cho user chỉnh sửa và xác nhận thay đổi data, sau đó đẩy data lên server.
- trên server ta viết 1 file PHP dùng để update data vào database được lưu trên server
- tương tự như việc thêm data mới từ app lên server, việc cập nhật chính là gửi data mới của id hiện tại lên server, ta sẽ vẫn dùng method __POST[]__
- __update.php__

```php
<?php
require "dbCon.php";

//
$id = $_POST['idSV'];
$hoten = $_POST['hotenSV'];
$namsinh = $_POST['namsinhSV'];
$diachi = $_POST['diachiSV'];

// câu lệnh truy vấn vào database và update database
$query = "UPDATE student SET hoten = '$hoten', namsinh = '$namsinh', diachi = '$diachi' WHERE id = '$id'";

// kiểm tra kết nối và truy vấn có thành công hay không
if ($connect, $query) {
  echo "Success";
} else {
  echo "Error";
}
?>
```

- khi user click vào __imgEdit__ sẽ kích hoạt 1 màn hình hiển thị thông tin đầy đủ của View đang chứa __imgEdit__ đó.
- ta tiến hành tạo 1 Empty Activity __UpdateSinhVienActivity__ và thiết kế layout cho nó

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:background="#B751E3"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".UpdateSinhVienActivity">

    <TextView
        android:id="@+id/textView2"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="44dp"
        android:text="Cập Nhật Thông Tin"
        android:textColor="#D10808"
        android:textSize="40sp"
        android:textStyle="bold"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.498"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <EditText
        android:id="@+id/editTextTextHoTenEdit"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="32dp"
        android:layout_marginTop="40dp"
        android:layout_marginEnd="32dp"
        android:ems="10"
        android:hint="Nhập họ tên"
        android:inputType="textPersonName"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.497"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/textView2" />

    <EditText
        android:id="@+id/editTextNamSinhEdit"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="32dp"
        android:layout_marginTop="32dp"
        android:layout_marginEnd="32dp"
        android:ems="10"
        android:hint="Nhập năm sinh"
        android:inputType="number|textPersonName"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.497"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/editTextTextHoTenEdit" />

    <EditText
        android:id="@+id/editTextDiaChiEdit"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="32dp"
        android:layout_marginTop="32dp"
        android:layout_marginEnd="32dp"
        android:ems="10"
        android:hint="Nhập địa chỉ"
        android:inputType="textPersonName"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.497"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/editTextNamSinhEdit" />

    <Button
        android:id="@+id/buttonCapNhat"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginStart="32dp"
        android:layout_marginTop="44dp"
        android:text="CẬP NHẬT"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/editTextDiaChiEdit" />

    <Button
        android:id="@+id/buttonHuyEdit"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="44dp"
        android:layout_marginEnd="32dp"
        android:text="HỦY"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/editTextDiaChiEdit" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

- do Adapter quản lý các View dùng để hiển thị các đối tượng SinhVien, nên trong method __getView()__ của __SinhVienAdapter__ ta bắt sự kiện click vào button edit.
- khi user click vào button edit trên MainActivity, màn hình dùng để update thông tin sinh viên sẽ được hiển thị với các thông tin đầy đủ ở item tương ứng, vì vậy ta sử dụng Intent và gửi object SinhVien qua màn hình update để user có thể thao tác update.
- __bắt sự kiện cho button edit trong getView() của SinhVienAdapter__

```java
// Bắt sự kiện khi click vào button imgEdit
        holder.imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UpdateSinhVienActivity.class);
                intent.putExtra("dataSinhVien", sinhVien);
                context.startActivity(intent);
            }
        });
```

- lưu ý: việc dùng Intent để gửi 1 object, thì class của object đó phải __implements Serializable__

```java
public class SinhVien implements Serializable { }
```

- xử lý logic trong __UpdateSinhVienActivity__

```java
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
```

___

CHỨC NĂNG XÓA DATA TRÊN SERVER TỪ APP

- để xóa data trên server, ở server ta cũng lưu trữ 1 file PHP dùng để thực hiện lệnh delete data có trong database ở server
- __delete.php__

```php
<?php
require "dbCon.php";

$id = $_POST['idSV'];

$query = "DELETE FROM student WHERE id = '$id'";

if (mysqli_query($connect, $query)) {
  echo "Success";
} else {
  echo "Error";
}
?>
```

- khi user click vào imgDelete, Dialog sẽ hiển thị hỏi user có muốn xóa hay không, và imgDelete đang ở MainActivity và chịu sự quản lý của Adapter
- vì vậy ta sẽ tiến hành bắt sự kiện user click imgDelete trong method __getView()__ của SinhVienAdapter

```java
        // Bắt sự kiện khi click vào button imgDelete
        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XacNhanXoa(sinhVien.getHoTen(), sinhVien.getId());
            }
        });
```

- khi user click vào imgDelete sẽ gọi đến method tạo Dialog hỏi user có muốn xóa hay không
- tạo thêm 1 method XacNhanXoa() dùng để hiện thị Dialog trong SinhVienAdapter

```java
    private void XacNhanXoa(String ten, int id) {
        AlertDialog.Builder dialogXoa = new AlertDialog.Builder(context);
        dialogXoa.setMessage("Bạn có muốn xóa " + ten + " không ???");
        dialogXoa.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                context.DeleteStudent(id);
            }
        });
        dialogXoa.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialogXoa.show();
    }
```

- ở trên, khi user click vào button Có, sẽ gọi đến method DeleteStudent() bên MainActivity, vì vậy, kiểu của biến toàn cục context ta nên thay trực tiếp bằng MainActivity trong Adapter
- khi Dialog xuất hiện, nếu user click vào không thì không thực hiện gì cả, nhưng khi click vào có, ta sẽ gọi 1 method trong MainActivity dùng để xóa data
- tạo method dùng để xóa data bên MainActivity

```java
    public void DeleteStudent(int idSV) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                urlDelete,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.trim().equals("Success")) {
                            Toast.makeText(MainActivity.this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                            GetData(urlGetdata); // tránh trường hợp data lặp lại vì gọi GetData(), trong method GetData() trước khi lấy data về array, ta nên clear() array trước khi thêm data và array
                        } else {
                            Toast.makeText(MainActivity.this, "Lỗi xóa", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("idSV", String.valueOf(idSV));

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
```

- khi xóa thành công, ta phải load data lại bằng cách gọi GetData() trong MainActivity, để tránh trường hợp GetData() sẽ lấy data trùng lặp từ server về đẩy vào array, trong GetData() ta phải clear() array trước khi thêm data vào array

```java
arraySinhVien.clear();
```