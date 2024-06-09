package com.example.dentiva.view.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dentiva.adapter.DoctorDetailsAdapter
import com.example.dentiva.data.local.DoctorDetails
import com.example.dentiva.databinding.FragmentDashboardBinding
import com.example.dentiva.viewmodel.DoctorViewModel
import com.example.dentiva.viewmodel.DoctorViewModelFactory
import com.example.dentiva.viewmodel.SharedViewModel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val doctorDetailsAdapter = DoctorDetailsAdapter()
    private val viewModel: DoctorViewModel by viewModels { DoctorViewModelFactory(requireActivity().application) }
    private lateinit var sharedViewModel: SharedViewModel
    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 10000
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
    private val locationSettingsRequest: LocationSettingsRequest =
        LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchView.setIconifiedByDefault(false)
        binding.rvDocterList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDocterList.adapter = doctorDetailsAdapter
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        sharedViewModel.refreshData.observe(viewLifecycleOwner, Observer { shouldRefresh ->
            if (shouldRefresh) {
                getLocationAndFetchData()
                sharedViewModel.setRefreshData(false) // Reset the flag
            }
        })

        val refresh = listOf(binding.tvRefresh, binding.ivRefresh)

        refresh.forEach {
            it.setOnClickListener {
                getLocationAndFetchData()
            }
        }

        checkPermissionsAndLocationSettings()
    }

    private fun checkPermissionsAndLocationSettings() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            checkLocationSettings()
        }
    }

    private fun checkLocationSettings() {
        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        settingsClient.checkLocationSettings(locationSettingsRequest).addOnSuccessListener {
            // Location settings are satisfied
            getLocationAndFetchData()
        }.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
                    exception.startResolutionForResult(requireActivity(), 1)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                    Toast.makeText(
                        requireContext(),
                        "Unable to start resolution for result: ${sendEx.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Location settings are inadequate, and cannot be fixed here. Fix in Settings.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun getLocationAndFetchData() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location: Location? = locationResult.lastLocation
                if (location != null) {
                    fetchData(location.latitude, location.longitude)
                    fusedLocationClient.removeLocationUpdates(this)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Unable to fetch location. Please ensure location services are enabled.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }, Looper.getMainLooper())
    }

    private fun fetchData(latitude: Double, longitude: Double) {
        binding.progressBar.visibility = View.VISIBLE

        viewModel.getDoctorDetails(latitude, longitude)
            .observe(viewLifecycleOwner, Observer { result ->
                binding.progressBar.visibility = View.GONE

                result.onSuccess { doctors ->
                    doctorDetailsAdapter.submitList(doctors)

                    // Save data to the local database
                    saveDoctorDetailsToDatabase(doctors)
                }
                result.onFailure { throwable ->
                    Toast.makeText(
                        requireContext(),
                        "Failed to fetch data: ${throwable.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun saveDoctorDetailsToDatabase(doctorDetailsList: List<DoctorDetails>) {
        lifecycleScope.launch {
            viewModel.insertDoctorDetails(doctorDetailsList)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkLocationSettings()
        } else {
            Toast.makeText(
                requireContext(),
                "Permission denied. Please allow location access to use this feature.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                // User agreed to make required location settings changes.
                (activity as MainActivity).notifyLocationPermissionGranted()
            } else {
                // User chose not to make required location settings changes.
                Toast.makeText(
                    requireContext(),
                    "Location services are required to fetch data.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
