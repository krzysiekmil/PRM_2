package pjwstk.s20124.prm_2.information

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import pjwstk.s20124.prm_2.R
import pjwstk.s20124.prm_2.authentication.AuthenticationActivity
import pjwstk.s20124.prm_2.databinding.ActivityAuthenticationBinding
import pjwstk.s20124.prm_2.databinding.ActivityInformationBinding

class InformationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInformationBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.logOutButton.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, AuthenticationActivity::class.java))
        }
    }
}