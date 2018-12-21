package ru.pe4encka.radio.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import ru.pe4encka.radio.R
import ru.pe4encka.radio.databinding.ActivityMainBinding
import ru.pe4encka.radio.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var model: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        model = ViewModelProviders.of(this).get(MainViewModel::class.java)
        binding.viewModel = model

        setFragment(StationsListFragment(), R.id.fragmentListContainer)
    }

    private fun setFragment(fragment: Fragment, container: Int) {
        supportFragmentManager
            .beginTransaction()
            .replace(container, fragment)
            .commit()
    }

}
