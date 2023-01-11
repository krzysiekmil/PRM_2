package pjwstk.s20124.prm_2.authentication

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import pjwstk.s20124.prm_2.R
import pjwstk.s20124.prm_2.databinding.FragmentRegistrationBinding

class RegistrationFragment : Fragment() {

    private var _binding: FragmentRegistrationBinding? = null
    private lateinit var auth: FirebaseAuth
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentRegistrationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleInputChanged()
        handleButton()
    }

    private fun handleButton() {
        val username = binding.inputUsername
        val password = binding.inputPassword

        binding.register.setOnClickListener {
            auth.createUserWithEmailAndPassword(username.text.trim().toString(), password.text.trim().toString())
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        Toast.makeText(activity, "User created :)", Toast.LENGTH_LONG).show()
                    }
                    else {
                        Toast.makeText(activity, "Something went wrong try again :(", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun handleInputChanged() {
        val username = binding.inputUsername
        val password = binding.inputPassword
        val repeat = binding.inputRepeatPassword

        username.addTextChangedListener { loginDataChanged() }
        password.addTextChangedListener { loginDataChanged() }
        repeat.addTextChangedListener { loginDataChanged() }

    }

    private fun loginDataChanged() {
        val username = binding.inputUsername
        val password = binding.inputPassword
        val repeat = binding.inputRepeatPassword

        val register = binding.register
        if (!isUserNameValid(username.text.toString())) {
            binding.inputContainer.error = getString(R.string.invalid_username)
        } else if (!isPasswordValid(password.text.toString())) {
            binding.passwordContainer.error = getString(R.string.invalid_password)
        }
        else if(!repeat.text.equals(password.text)){
            binding.passwordContainer.error = getString(R.string.repeat_password_not_same)
            binding.repeatContainer.error = getString(R.string.repeat_password_not_same)
        }
        else {
            binding.inputContainer.error = null
            binding.passwordContainer.error = null
            binding.repeatContainer.error = null
            register.isEnabled = true
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
        return password.isBlank() || password.length > 5
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}