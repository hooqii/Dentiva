package com.example.dentiva.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.dentiva.R
import com.example.dentiva.data.local.DoctorDetails

class DoctorDetailsAdapter :
    ListAdapter<DoctorDetails, DoctorDetailsAdapter.DoctorViewHolder>(DoctorDiffCallback()) {

    inner class DoctorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewDoctor: ImageView = itemView.findViewById(R.id.imageViewDoctor)
        val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        val textViewAddress: TextView = itemView.findViewById(R.id.textViewAddress)
        val textViewPhoneNumber: TextView = itemView.findViewById(R.id.textViewPhone)
        val textViewRating: TextView = itemView.findViewById(R.id.textViewRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_doctor_location, parent, false)
        return DoctorViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        val currentDoctor = getItem(position)
        holder.textViewName.text = currentDoctor.displayName
        holder.textViewAddress.text = currentDoctor.address
        holder.textViewPhoneNumber.text = currentDoctor.phoneNumber
        holder.textViewRating.text = "${currentDoctor.rating} star rating"

        val photos = currentDoctor.photos
        if (photos != null && photos.isNotEmpty()) {
            Glide.with(holder.itemView.context).load(photos[0].urlImage).transform(CircleCrop())
                .into(holder.imageViewDoctor)
        } else {
            Glide.with(holder.itemView.context).load(R.drawable.doctor).transform(CircleCrop())
                .into(holder.imageViewDoctor)
        }
    }

    class DoctorDiffCallback : DiffUtil.ItemCallback<DoctorDetails>() {
        override fun areItemsTheSame(oldItem: DoctorDetails, newItem: DoctorDetails): Boolean {
            return oldItem.displayName == newItem.displayName
        }

        override fun areContentsTheSame(oldItem: DoctorDetails, newItem: DoctorDetails): Boolean {
            return oldItem == newItem
        }
    }
}
