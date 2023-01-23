package pjwstk.s20124.prm_2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import pjwstk.s20124.prm_2.authentication.AuthenticationActivity
import pjwstk.s20124.prm_2.databinding.ActivityMainBinding
import pjwstk.s20124.prm_2.information.InformationActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) {
            Log.d(null,"Not finding user")
            startActivity(Intent(this, AuthenticationActivity::class.java))
        }
        else {
            Log.d(null, "Currently logged user")
            startActivity(Intent(this, InformationActivity::class.java))
        }
    }
}