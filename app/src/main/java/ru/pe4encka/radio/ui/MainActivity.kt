package ru.pe4encka.radio.ui

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import ru.pe4encka.radio.R
import ru.pe4encka.radio.databinding.ActivityMainBinding
import ru.pe4encka.radio.repository.Repository
import ru.pe4encka.radio.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var model: MainViewModel
    private var tabPosition:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v("Application","MainActivity onCreate")
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        Log.v("Application","MainActivity onCreate setContentView OK")
        model = ViewModelProviders.of(this).get(MainViewModel::class.java)
        Log.v("Application","MainActivity onCreate ViewModelProviders OK")
        binding.apply {
            viewModel = model
            bottomMenu.apply {
                itemIconTintList = resources.getColorStateList(R.color.bnv_base, theme)
                setOnNavigationItemSelectedListener { menuItemSelected(it) }
                tabPosition = Repository.loadTabPosition(this@MainActivity)
                selectedItemId = when(tabPosition) {
                    0 -> R.id.menu_catalog_id
                    1 -> R.id.menu_recent_id
                    else -> R.id.menu_catalog_id
                }
            }
        }
        setSearchListeners()
        Log.v("Application","MainActivity onCreate OK")
    }

    private fun menuItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_catalog_id -> {
                setFragment(StationsListFragment(), R.id.fragmentListContainer)
                model.onSelectTab(0)
                tabPosition = 0
            }
            R.id.menu_recent_id -> {
                setFragment(RecentListFragment(), R.id.fragmentListContainer)
                model.onSelectTab(1)
                tabPosition = 1
            }
        }
        return true

    }

    private fun setSearchListeners() = binding.search.apply {
        setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                model.onSearch(query ?: "")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })

        setOnCloseListener {
            model.onSearch("")
            false
        }

    }

    private fun setFragment(fragment: Fragment, container: Int) {
        supportFragmentManager
            .beginTransaction()
            .replace(container, fragment)
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        Repository.saveTabPosition(this,tabPosition)
    }
}
