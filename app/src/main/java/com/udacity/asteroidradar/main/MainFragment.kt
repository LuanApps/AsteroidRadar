package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.work.*
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.util.AsteroidAdapter
import java.util.concurrent.TimeUnit

class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel

    private lateinit var asteroidAdapter: AsteroidAdapter

    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val application = requireNotNull(this.activity).application
        val viewModelFactory = MainViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]


        binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        asteroidAdapter = AsteroidAdapter(AsteroidAdapter.AsteroidListener { asteroidId ->
            viewModel.onAsteroidClicked(asteroidId)
        })

        viewModel.navigateToDetails.observe(viewLifecycleOwner, Observer { asteroid ->
            asteroid?.let {
                this.findNavController().navigate(MainFragmentDirections
                    .actionShowDetail(asteroid)
                )
                viewModel.onAsteroidNavigated()
            }
        })

        binding.asteroidRecycler.adapter = asteroidAdapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenu()

        // Observe changes in the list of asteroids and update the adapter
        viewModel.asteroids.observe(viewLifecycleOwner) { asteroids ->
            asteroidAdapter.submitList(asteroids)
        }

        // update the imageView with Picasso Library in the ImageOfTheDay view
        val imageView = binding.activityMainImageOfTheDay
        val imageTitle = binding.imageTitle
        viewModel.pictureOfDay.observe(viewLifecycleOwner) { picture ->
            picture?.let {
                Picasso.get()
                    .load(picture.url)
                    .placeholder(R.drawable.placeholder_picture_of_day)
                    .error(R.drawable.placeholder_picture_of_day)
                    .into(imageView)
            }
            imageTitle.text = picture?.title ?: "No Picture For Today"
            imageView.contentDescription = "Picture of the day: ${picture?.title ?: "No Picture For Today"}"
        }

        //Worker to download and save today asteroid into database one time per day.

        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()

        val repeatingRequest = PeriodicWorkRequestBuilder<RefreshAsteroidsWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            "RefreshAsteroidsWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest
        )
    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onPrepareMenu(menu: Menu) {
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_overflow_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.show_next_week_asteroids -> {
                        true
                    }
                    R.id.show_today_asteroids -> {
                        true
                    }
                    R.id.show_saved_asteroids -> {
                        true
                    }
                    else -> {true}
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

}
