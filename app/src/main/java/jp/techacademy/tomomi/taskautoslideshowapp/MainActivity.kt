package jp.techacademy.tomomi.taskautoslideshowapp

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import android.widget.CursorAdapter
import android.database.Cursor
import android.provider.MediaStore
import android.content.ContentUris
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.Closeable
import java.util.*


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val PERMISSIONS_REQUEST_CODE = 100
    var numbers = arrayOf<Int>()
    var cursor: Cursor? = null

    var button_enable: Boolean? = true
    private var mTimer: Timer? = null
    private var mTimerSec = 0
    private var mHandler = Handler()
    var fieldIndex = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        next_button.setOnClickListener(this)
        prev_button.setOnClickListener(this)
        play_button.setOnClickListener(this)
        //getContentsInfo()


        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo()
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE
                )
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getContentsInfo()
            } else {
                Toast.makeText(this, "許可をしないとスライドは使えません", Toast.LENGTH_LONG).show()
                next_button.isEnabled = false
                prev_button.isEnabled = false
                play_button.isEnabled = false
            }
        }

    }

    override fun onClick(v: View) {
        // クリック時の処理
        // 画像の情報を取得する　


        if (v.id == R.id.next_button) {
            if (cursor!!.isLast()) {
                cursor?.moveToFirst()
                Log.d(cursor.toString(), "moveImgFirst")

            } else {
                cursor?.moveToNext()
                Log.d(cursor.toString(), "moveImgNext")
            }
            fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
            var imageUri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                cursor!!.getLong(fieldIndex)
            )
            imageView.setImageURI(imageUri)

        } else if (v.id == R.id.prev_button) {

            if (cursor!!.isFirst()) {
                cursor?.moveToLast()
                Log.d(cursor.toString(), "moveImgLast")
                //Log.d("最初",cursor.isLast().toString())
            } else {
                //cursor.moveToPrevious()
                Log.d(cursor.toString(), "moveImgPrevious")

                cursor?.moveToPrevious()
            }
            fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)

            var imageUri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                cursor!!.getLong(fieldIndex)
            )
            //Log.d("gazouaaa",cursor!!.getLong(fieldIndex))
            imageView.setImageURI(imageUri)

        } else if (v.id == R.id.play_button) {

            if (button_enable == false) {
                // 止めるとき
                button_enable = true
                next_button.isEnabled = true
                prev_button.isEnabled = true
                play_button.text = "再生"
                mTimer!!.cancel()
                mTimer = null

            } else {
                // 動かすとき
                button_enable = false
                next_button.isEnabled = false
                prev_button.isEnabled = false
                play_button.text = "停止"
                mTimer = Timer()


                // タイマーの始動
                mTimer?.schedule(object : TimerTask() {
                    override fun run() {
                        mTimerSec += 1
                        mHandler.post {
                            if (cursor!!.isLast()) {
                                cursor?.moveToFirst()
                                Log.d(cursor.toString(), "moveImgFirst")

                            } else {
                                cursor?.moveToNext()
                                Log.d(cursor.toString(), "moveImgNext")
                            }
                            fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                            var imageUri = ContentUris.withAppendedId(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                cursor!!.getLong(fieldIndex)
                            )
                            imageView.setImageURI(imageUri)
                        }
                    }
                }, 2000, 2000)
            }
        }
    }

    private fun getContentsInfo() {
        // 画像の情報を取得する
        val resolver = contentResolver
        cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目(null = 全項目)
            null, // フィルタ条件(null = フィルタなし)
            null, // フィルタ用パラメータ
            null // ソート (null ソートなし)
        )
        cursor?.moveToFirst()

        Log.d("ここにきているか", "koko")
    }

}
