package pjwstk.s20124.prm_2.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import pjwstk.s20124.prm_2.R
import pjwstk.s20124.prm_2.databinding.FragmentLoginBinding
import pjwstk.s20124.prm_2.information.InformationActivity


class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private var SIGN_IN_CODE = 1

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    private fun initGoogleSignInClient() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = activity?.let { GoogleSignIn.getClient(it, googleSignInOptions) }!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initGoogleSignInClient()
        handleInputChanged()
        handleButtons()

    }


    private fun handleButtons() {

        handleSignInButton()
        handleGoogleSignIn()
        handleRegisterButton()

    }

    private fun handleSignInButton() {
        val username = binding.username.text.trim().toString()
        val password = binding.password.text.trim().toString()
        binding.login.setOnClickListener {
            binding.loading.isEnabled = true
            auth.signInWithEmailAndPassword(username, password).addOnCompleteListener {handleSignIn(it)}
        }
    }

    private fun handleRegisterButton(){
        binding.register.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
        }
    }

    private fun handleGoogleSignIn() {
        binding.googleSignIn.setOnClickListener { signInGoogle() }
    }

    private fun signInGoogle() {
        binding.loading.isEnabled = true
        val signInIntent: Intent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, SIGN_IN_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            SIGN_IN_CODE -> {
                try {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    val googleSignInAccount = task.result!!
                    getGoogleAuthCredential(googleSignInAccount)
                } catch (e: ApiException) {
                    e.message?.let { Log.d("", it) }
                }
                finally {
                    binding.loading.isEnabled = false
                }
            }
        }
    }

    private fun getGoogleAuthCredential(googleSignInAccount: GoogleSignInAccount) {
        val googleTokenId = googleSignInAccount.idToken!!
        val googleAuthCredential: AuthCredential = GoogleAuthProvider.getCredential(googleTokenId, null)
        auth.signInWithCredential(googleAuthCredential).addOnCompleteListener{ handleSignIn(it) }
    }

    private fun handleSignIn(result: Task<AuthResult>) {

        if (result.isSuccessful) {
            startActivity(Intent(activity, InformationActivity::class.java))
        } else {
            Log.d("LOGIN", "Unsuccessfully")
            this.view?.let { view ->
                Snackbar.make(
                    view,
                    "Wrong credentials",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }

    private fun handleInputChanged() {
        val username = binding.username
        val password = binding.password

        username.addTextChangedListener { loginDataChanged() }
        password.addTextChangedListener { loginDataChanged() }

    }

    private fun loginDataChanged() {
        val username = binding.username
        val password = binding.password
        val login = binding.login
        if (!isUserNameValid(username.text.toString())) {
            binding.usernameContainer.error = getString(R.string.invalid_username)
        } else if (!isPasswordValid(password.text.toString())) {
            binding.passwordContainer.error = getString(R.string.invalid_password)
        } else {
            binding.usernameContainer.error = null
            binding.passwordContainer.error = null

            login.isEnabled = true
        }
    }

    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}