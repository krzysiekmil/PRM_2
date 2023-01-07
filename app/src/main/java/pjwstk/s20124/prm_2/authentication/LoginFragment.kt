package pjwstk.s20124.prm_2.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import pjwstk.s20124.prm_2.R
import pjwstk.s20124.prm_2.databinding.ActivityMainBinding
import pjwstk.s20124.prm_2.databinding.FragmentLoginBinding
import pjwstk.s20124.prm_2.information.InformationActivity


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private lateinit var auth: FirebaseAuth
    private val binding get() = _binding!!



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        arguments?.let {}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleInputChanged()
        handleButtons()

    }

    private fun handleButtons() {
        binding.login.setOnClickListener {
            binding.loading.isEnabled = true
            val username = binding.username
            val password = binding.password

            auth.signInWithEmailAndPassword(username.text.trim().toString(), password.text.trim().toString()).addOnCompleteListener {
                if (it.isSuccessful) {
                    startActivity(Intent(activity, InformationActivity::class.java))
                }
                else {
                    Log.d("LOGIN", "Unsuccessfully")
                    this.view?.let { view -> Snackbar.make(view, "Wrong credentials", Toast.LENGTH_LONG).show() }
                }
            }
        }

        binding.register.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
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
            username.error = getString(R.string.invalid_username)
        } else if (!isPasswordValid(password.text.toString())) {
            password.error = getString(R.string.invalid_password)
        } else {
            login.isEnabled = true
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}