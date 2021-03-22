package com.pnp.myafmusikplayer.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.android.material.navigation.NavigationView
import com.pnp.myafmusikplayer.R
import com.pnp.myafmusikplayer.adapter.HomeAdapter
import com.pnp.myafmusikplayer.adapter.ListLaguAdapter.onSelectData
import com.pnp.myafmusikplayer.model.ModelListLagu
import com.pnp.myafmusikplayer.networking.Api
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.activity_list_lagu.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.app_bar_main.rvListMusic
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    onSelectData, HomeAdapter.onSelectData {
    var HomeAdapter: HomeAdapter? = null
    var progressDialog: ProgressDialog? = null
    var modelListLagu: MutableList<ModelListLagu> = ArrayList()


    private lateinit var drawer : DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        }

        if (Build.VERSION.SDK_INT >= 21) {
            ListLaguActivity.setWindowFlag(
                this,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                false
            )
            window.statusBarColor = Color.TRANSPARENT
        }

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        toolbar.setTitle("Home")
        setSupportActionBar(toolbar)
        assert(supportActionBar != null)

        drawer = findViewById(R.id.home_drawer_layout)

        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("Mohon Tunggu")
        progressDialog!!.setCancelable(false)
        progressDialog!!.setMessage("Sedang menampilkan data...")

        rvListMusic!!.setHasFixedSize(true)
        rvListMusic!!.setLayoutManager(LinearLayoutManager(this))

        getListMusic()

    }

    private fun getListMusic() {
        progressDialog!!.show()
        AndroidNetworking.get(Api.ListMusic)
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    try {
                        progressDialog!!.dismiss()
                        val playerArray = response.getJSONArray("post")
                        for (i in 0 until playerArray.length()) {
                            if (i > 3) {
                                val temp = playerArray.getJSONObject(i)
                                val dataApi = ModelListLagu()
                                dataApi.strId = temp.getString("id")
                                dataApi.strCoverLagu = temp.getString("coverartikel")
                                dataApi.strNamaBand = temp.getString("namaband")
                                dataApi.strJudulMusic = temp.getString("judulmusic")
                                modelListLagu.add(dataApi)
                                showListMusic()
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(this@HomeActivity,
                            "Gagal menampilkan data!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onError(anError: ANError) {
                    progressDialog!!.dismiss()
                    Toast.makeText(this@HomeActivity,
                        "Tidak ada jaringan internet!", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showListMusic() {
        HomeAdapter = HomeAdapter(this@HomeActivity, modelListLagu, this)
        rvListMusic!!.adapter = HomeAdapter
    }


    override fun onSelected(modelListLagu: ModelListLagu) {
        val intent = Intent(this@HomeActivity, DetailLaguActivity::class.java)
        intent.putExtra("detailLagu", modelListLagu)
        startActivity(intent)
    }


    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if  (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item : MenuItem): Boolean {

        val id : Int = item.itemId

        if (id ==  R.id.nav_home ){
            Toast.makeText(this,"Home", Toast.LENGTH_LONG).show()

        }else if (id == R.id.nav_list_lagu){
            val intentlist = Intent(this, ListLaguActivity::class.java)
            startActivity(intentlist)
            Toast.makeText(this,"ListLagu", Toast.LENGTH_LONG).show()

        }else if (id == R.id.nav_about){
            val intentabout = Intent(this, AboutActivity::class.java)
            startActivity(intentabout)
            Toast.makeText(this,"About", Toast.LENGTH_LONG).show()

        }

        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        }else{
            super.onBackPressed()
        }

    }
    companion object {
        fun setWindowFlag(activity: Activity, bits: Int, on: Boolean) {
            val win = activity.window
            val winParams = win.attributes
            if (on) {
                winParams.flags = winParams.flags or bits
            } else {
                winParams.flags = winParams.flags and bits.inv()
            }
            win.attributes = winParams
        }
    }
}