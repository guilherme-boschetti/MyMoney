package com.guilhermeb.mymoney.view.money.activity

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.guilhermeb.mymoney.R
import com.guilhermeb.mymoney.databinding.ActivityMoneyHostBinding
import com.guilhermeb.mymoney.databinding.NavHeaderDrawerBinding
import com.guilhermeb.mymoney.view.app.activity.AbstractActivity
import com.guilhermeb.mymoney.viewmodel.money.MoneyViewModel
import com.guilhermeb.mymoney.viewmodel.money.MoneyViewModelFactory

class MoneyHostActivity : AbstractActivity() {

    private lateinit var binding: ActivityMoneyHostBinding

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var mDrawerToggle: ActionBarDrawerToggle
    private lateinit var mDrawerLayout: DrawerLayout

    private lateinit var mDrawerMenuMonths: List<String>

    private val moneyViewModel by lazy {
        ViewModelProvider(
            this,
            MoneyViewModelFactory(true)
        )[MoneyViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoneyHostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(R.string.app_name)

        initScreen()
        observeProperties()
        fetchDrawerData()
        // == -- == -- == -- == -- == -- == --
    }

    private fun initScreen() {
        // AppBar ----------
        setSupportActionBar(binding.toolbar)

        binding.toolbar.setNavigationOnClickListener {
            if (mDrawerToggle.isDrawerIndicatorEnabled) {
                mDrawerLayout.openDrawer(GravityCompat.START)
            } else {
                onBackPressed()
            }
        }

        // == -- == -- == -- Drawer Menu == -- == -- == --
        mDrawerLayout = binding.drawerLayout
        mDrawerToggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.open_slide_menu,
            R.string.close_slide_menu
        )
        mDrawerToggle.setToolbarNavigationClickListener {
            if (mDrawerToggle.isDrawerIndicatorEnabled) {
                mDrawerLayout.openDrawer(GravityCompat.START)
            } else {
                onBackPressed()
            }
        }
        binding.drawerLayout.addDrawerListener(mDrawerToggle)
        mDrawerToggle.syncState()

        val navHeaderDrawerBinding: NavHeaderDrawerBinding =
            NavHeaderDrawerBinding.bind(binding.navViewDrawer.getHeaderView(0))
        navHeaderDrawerBinding.txtEmail.text = moneyViewModel.getCurrentUserEmail()


        /*val drawerMenuMonths = moneyViewModel.getAllMonthsByUser(moneyViewModel.getCurrentUserEmail()!!)
        for (i in 0 until drawerMenuMonths.size) {
            val textMenuDrawer = drawerMenuMonths[i]
            // groupId, itemId, order, title
            binding.navViewDrawer.menu.add(R.id.group_menu_drawer, i, i, textMenuDrawer)
                .setIcon(R.drawable.ic_baseline_calendar_month_24)
        }

        binding.navViewDrawer.menu.setGroupCheckable(R.id.group_menu_drawer, true, true)*/

        binding.navViewDrawer.setNavigationItemSelectedListener {
            if (this::mDrawerMenuMonths.isInitialized) {
                moneyViewModel.setSelectedYearAndMonthName(mDrawerMenuMonths[it.itemId])
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            return@setNavigationItemSelectedListener true
        }
    }

    private fun observeProperties() {
        moneyViewModel.drawerMonths.observe(this) { drawerMenuMonths ->
            mDrawerMenuMonths = drawerMenuMonths

            binding.navViewDrawer.menu.clear()

            for (i in drawerMenuMonths.indices) {
                val textMenuDrawer = drawerMenuMonths[i]
                // groupId, itemId, order, title
                binding.navViewDrawer.menu.add(R.id.group_menu_drawer, i, i, textMenuDrawer)
                    .setIcon(R.drawable.ic_baseline_calendar_month_24)
            }

            binding.navViewDrawer.menu.setGroupCheckable(R.id.group_menu_drawer, true, true)
        }

        moneyViewModel.firstDrawerMenuItemChecked.observe(this) {
            if (it >= 0) {
                binding.navViewDrawer.menu.getItem(it).isChecked = true
            }
        }
    }

    private fun fetchDrawerData() {
        moneyViewModel.getAllMonthsByUser(moneyViewModel.getCurrentUserEmail()!!)
    }

    fun showBackButton(isBack: Boolean) {
        mDrawerToggle.isDrawerIndicatorEnabled = !isBack
        supportActionBar!!.setDisplayHomeAsUpEnabled(isBack)
        mDrawerToggle.syncState()
        if (isBack) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        } else {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        }
    }

    override fun onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_item_detail)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun recreate() {
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}