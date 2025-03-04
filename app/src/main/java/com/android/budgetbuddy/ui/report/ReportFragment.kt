package com.android.budgetbuddy.ui.report

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.budgetbuddy.R
import com.android.budgetbuddy.data.ViewModelFactory
import com.android.budgetbuddy.data.adapter.TransactionAdapter
import com.android.budgetbuddy.data.model.Transaction
import com.android.budgetbuddy.data.utils.loadImageUrl
import com.android.budgetbuddy.databinding.FragmentReportBinding
import com.android.budgetbuddy.ui.auth.LoginActivity
import com.android.budgetbuddy.ui.transaction.TransactionActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class ReportFragment:Fragment() {


    private lateinit var binding: FragmentReportBinding
    private val transactionAdapter = TransactionAdapter()
    private var database: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var auth: FirebaseAuth = Firebase.auth

    private val decimalFormat = DecimalFormat("#,##0")

    private var amountExpense: Double = 0.0
    private var amountIncome: Double = 0.0
    private var allTimeExpense: Double = 0.0
    private var allTimeIncome: Double = 0.0

    private var dateStart: Long = 0
    private var dateEnd: Long = 0

    private var transactionListener: ListenerRegistration? = null

    private val viewModel: ReportViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.btnLogout.setOnClickListener {
            showExitConfirmationDialog()
        }


        getFullTransaction()

        setupRecyclerView()

        retrieveData()

        setInitDate()

        chartMenu()

        loadTransactionData()

        Handler().postDelayed({
            showAllTimeRecap()
            setupPieChart()
            setupBarChart()
        }, 200)

        dateRangePicker()

//        viewModel.getNotificationSettings().observe(viewLifecycleOwner) { isNotificationActive ->
//            binding.switchAlarm.isChecked = isNotificationActive
//        }


//        binding.switchAlarm.setOnCheckedChangeListener { _, isChecked ->
//            viewModel.saveNotificationSetting(isChecked)
//            if (isChecked) {
//                NotificationUtils.scheduleDailyNotification(requireContext())
//            } else {
//                NotificationUtils.cancelNotifications(requireContext())
//            }
//        }

        swipeRefresh()




    }

    private fun setupRecyclerView() {
        binding.rvTransaction.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = transactionAdapter
        }
    }

    private fun getFullTransaction(){

        binding.viewMore.setOnClickListener {
            val intent = Intent(requireContext(), TransactionActivity::class.java)
            startActivity(intent)
        }
    }

    private fun swipeRefresh() {
        val swipeRefreshLayout: SwipeRefreshLayout = binding.swipeRefresh
        swipeRefreshLayout.setOnRefreshListener {
            retrieveData()
            showAllTimeRecap()
            setupPieChart()
            setupBarChart()
            loadTransactionData()
            setupRecyclerView()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun loadTransactionData() {
        transactionListener?.remove() // Remove any existing listener

        val userId = auth.currentUser?.uid
        if (userId != null) {
            val transactionRef = database
                .collection("user")
                .document(userId)
                .collection("transaction")
                .orderBy("date", Query.Direction.DESCENDING) // Ensure newest transactions come first
                .limit(3) // Fetch only 3 transactions

            transactionListener = transactionRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("HomeFragment", "Error fetching transactions", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val transactionList = snapshot.documents.mapNotNull { it.toObject(Transaction::class.java) }

                    transactionAdapter.submitList(transactionList)

                    //binding.viewEmpty.root.visibility = if (transactionList.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        } else {
            Toast.makeText(requireContext(), "User not logged in.", Toast.LENGTH_SHORT).show()
        }
    }



    private fun showExitConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Konfirmasi")
            .setMessage("Apakah Anda yakin ingin keluar?")
            .setPositiveButton("Ya") { _, _ ->

                //categoryViewModel.deleteAllCategories()
                // Clear the shared preference for login status
                viewModel.saveLoginState("isLoggedIn", false)
                // Logout from Firebase
                auth.signOut()
                // Redirect to LoginActivity
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)

            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


    private fun retrieveData() {
        val userId = auth.currentUser!!.uid


        val dataUser = database.collection("user").document(userId)
        dataUser.get().addOnSuccessListener {
            val email = it.get("email").toString()
            val fotoProfil = it.get("fotoProfil").toString()

            // Extract the part of the email before the "@"
            val userName = email.substringBefore("@")

            binding.apply {
                imgProfile.loadImageUrl(fotoProfil, requireContext())
                tvEmail.text = email
                tvName.text = userName
            }
        }
    }

    private fun chartMenu() {
        val chartMenuRadio: RadioGroup = requireView().findViewById(R.id.RadioGroup)
        val pieChart: PieChart = requireView().findViewById(R.id.pieChart)
        val barChart: BarChart = requireView().findViewById(R.id.barChart)

        chartMenuRadio.setOnCheckedChangeListener { _, checkedID ->
            if (checkedID == R.id.rbBarChart){
                barChart.visibility = View.VISIBLE
                pieChart.visibility = View.GONE
            }
            if (checkedID == R.id.rbPieChart){
                barChart.visibility = View.GONE
                pieChart.visibility = View.VISIBLE
            }
        }
    }

    private fun setInitDate() {
        val dateRangeButton: Button = requireView().findViewById(R.id.buttonDate)

        val currentDate = Date()
        val cal: Calendar = Calendar.getInstance(TimeZone.getDefault())
        cal.time = currentDate

        val startDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH)
        cal.set(Calendar.DAY_OF_MONTH, startDay)
        val startDate = cal.time
        dateStart= startDate.time

        val endDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        cal.set(Calendar.DAY_OF_MONTH, endDay)
        val endDate = cal.time
        dateEnd= endDate.time

        fetchAmount(dateStart, dateEnd)
        dateRangeButton.text = "This Month"
    }

    private fun dateRangePicker() {
        val dateRangeButton: Button = requireView().findViewById(R.id.buttonDate)
        dateRangeButton.setOnClickListener {

            val datePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Pilih Tanggal")
                .setSelection(
                    Pair(
                        dateStart,
                        dateEnd
                    )
                ).build()
            datePicker.show(parentFragmentManager, "DatePicker")


            datePicker.addOnPositiveButtonClickListener {

                val dateString = datePicker.selection.toString()
                val date: String = dateString.filter { it.isDigit() }
                //divide the start and end date value :
                val pickedDateStart = date.substring(0,13).toLong()
                val pickedDateEnd  = date.substring(13).toLong()
                dateRangeButton.text = convertDate(pickedDateStart, pickedDateEnd)
                fetchAmount(pickedDateStart, pickedDateEnd)

                Handler().postDelayed({
                    setupPieChart()
                    setupBarChart()
                }, 200)
            }
        }
    }

    private fun showAllTimeRecap() {
        //val tvNetAmount: TextView = requireView().findViewById(R.id.netAmount)
        val tvAmountExpense: TextView = requireView().findViewById(R.id.expenseAmount)
        val tvAmountIncome: TextView = requireView().findViewById(R.id.incomeAmount)

//        tvNetAmount.text = "${allTimeIncome-allTimeExpense}"
//        tvAmountExpense.text = "$allTimeExpense"
//        tvAmountIncome.text = "$allTimeIncome"
        //tvNetAmount.text = decimalFormat.format(allTimeIncome - allTimeExpense)
        tvAmountExpense.text = decimalFormat.format(allTimeExpense)
        tvAmountIncome.text = decimalFormat.format(allTimeIncome)
    }

    private fun setupBarChart() {
        val netAmountRangeDate: TextView = requireView().findViewById(R.id.netAmountRange)
        netAmountRangeDate.text = decimalFormat.format(amountIncome - amountExpense)

        val barChart: BarChart = requireView().findViewById(R.id.barChart)

        val barEntries = arrayListOf<BarEntry>()
        barEntries.add(BarEntry(1f, amountExpense.toFloat()))
        barEntries.add(BarEntry(2f, amountIncome.toFloat()))

        val xAxisValue= arrayListOf<String>("","Pengeluaran", "Pemasukan")

        barChart.animateXY(500, 500)
        barChart.description.isEnabled = false
        barChart.setDrawValueAboveBar(true)
        barChart.setDrawBarShadow(false)
        barChart.setPinchZoom(false)
        barChart.isDoubleTapToZoomEnabled = false
        barChart.setScaleEnabled(false)
        barChart.setFitBars(true)
        barChart.legend.isEnabled = false

        barChart.axisRight.isEnabled = false
        barChart.axisLeft.isEnabled = false
        barChart.axisLeft.axisMinimum = 0f


        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.valueFormatter = com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xAxisValue)

        val barDataSet = BarDataSet(barEntries, "")
        barDataSet.setColors(
            resources.getColor(R.color.hijauu),
            resources.getColor(R.color.hijautuaa)
        )
        barDataSet.valueTextColor = Color.BLACK
        barDataSet.valueTextSize = 15f
        barDataSet.isHighlightEnabled = false

        val barData = BarData(barDataSet)
        barData.barWidth = 0.5f

        barChart.data = barData
    }


    private fun setupPieChart(){

        val pieChart: PieChart = requireView().findViewById(R.id.pieChart)

        val pieEntries = arrayListOf<PieEntry>()
        pieEntries.add(PieEntry(amountExpense.toFloat(), "Pengeluaran"))
        pieEntries.add(PieEntry(amountIncome.toFloat(), "Pemasukan"))


        pieChart.animateXY(500, 500)

        //setup pie chart colors
        val pieDataSet = PieDataSet(pieEntries,"")
        pieDataSet.setColors(
            resources.getColor(R.color.hijauu),
            resources.getColor(R.color.hijautuaa)
        )

        pieChart.description.isEnabled = false
        pieChart.legend.isEnabled = false
        pieChart.setUsePercentValues(true)
        pieChart.setEntryLabelTextSize(12f)
        pieChart.setEntryLabelColor(Color.WHITE)
        pieChart.holeRadius = 46f

        // Setup pie data
        val pieData = PieData(pieDataSet)
        pieData.setDrawValues(true)
        pieData.setValueFormatter(PercentFormatter(pieChart))
        pieData.setValueTextSize(12f)
        pieData.setValueTextColor(Color.WHITE)

        pieChart.data = pieData
        pieChart.invalidate()
    }

    private fun fetchAmount(dateStart: Long, dateEnd: Long) {
        var amountExpenseTemp = 0.0
        var amountIncomeTemp = 0.0
        val userId = auth.currentUser?.uid
        val transactionRef = userId?.let {
            FirebaseFirestore.getInstance().collection("user").document(it).collection("transaction")
        }

        if (transactionRef != null) {
            transactionRef
                .whereGreaterThan("date", dateStart - 86400000)
                .whereLessThanOrEqualTo("date", dateEnd)
                .get()
                .addOnSuccessListener { documents ->
                    val transactionList = arrayListOf<Transaction>()

                    for (document in documents) {
                        val transactionData = document.toObject(Transaction::class.java)
                        transactionList.add(transactionData)
                    }

                    if (transactionList.isEmpty()) {
                        amountExpense = 0.0
                        amountIncome = 0.0
                        allTimeExpense = 0.0
                        allTimeIncome = 0.0
                    } else {
                        for (transaction in transactionList) {
                            if (transaction.type == 1) {
                                amountExpenseTemp += transaction.amount ?: 0.0
                            } else if (transaction.type == 2) {
                                amountIncomeTemp += transaction.amount ?: 0.0
                            }
                        }
                    }

                    amountExpense = amountExpenseTemp
                    amountIncome = amountIncomeTemp
                    allTimeExpense = amountExpenseTemp
                    allTimeIncome = amountIncomeTemp

                    // Now that data is fetched, update the UI
                    showAllTimeRecap()
                    setupPieChart()
                    setupBarChart()
                }
                .addOnFailureListener { e ->
                    Log.w("FirestoreError", "Error fetching transactions: ${e.message}")
                }
        }
    }




    private fun convertDate(dateStart: Long, dateEnd: Long): String {
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        val date1 = Date(dateStart)
        val date2 = Date(dateEnd)
        val result1 = simpleDateFormat.format(date1)
        val result2 = simpleDateFormat.format(date2)
        return "$result1 - $result2"
    }

}