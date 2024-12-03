package com.example.tonottodo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tonottodo.databinding.FragmentMyPageBinding
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter

class MyPageFragment : Fragment() {
    lateinit var binding: FragmentMyPageBinding
    private var isRecentOrder = true

    data class Seesaw(
        val title: String,
        val toDoCount: Int,
        val notToDoCount: Int,
        val imageResId: Int
    )

    private val ongoingData = listOf(
        Seesaw("킹왕짱 대학생 되기", 2, 1, R.drawable.img_seesaw_21), // 밸런스: 2
        Seesaw("건강한 식습관 형성하기", 3, 3, R.drawable.img_seesaw_33), // 밸런스: 6
        Seesaw("갓생 개발자 되기", 3, 0, R.drawable.img_seesaw_30), // 밸런스: 0
        Seesaw("게임 중독 벗어나기", 1, 3, R.drawable.img_seesaw_13), // 밸런스: 2
        Seesaw("킹왕짱 헬스인 되기", 0, 3, R.drawable.img_seesaw_03), // 밸런스: 2
        Seesaw("꼼꼼 인간 되기", 2, 2, R.drawable.img_seesaw_22) // 밸런스: 4
    )

    private val completedData = listOf(
        Seesaw("독서 습관 만들기", 1, 0, R.drawable.img_seesaw_10), // 밸런스: 2
        Seesaw("규칙적인 생활습관 만들기", 1, 3, R.drawable.img_seesaw_13) // 밸런스: 4
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyPageBinding.inflate(layoutInflater)

        binding.checkboxInProgress.isChecked = true

        setupCheckBoxListeners()
        setupDropdownMenu()
        updateFilteredBoxes()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBarChart()

        binding.clMyPageSeesaw1.setOnClickListener {
            findNavController().navigate(R.id.action_my_page_to_seesaw_record)
        }
    }

    // barChart 초기화
    private fun initBarChart() {
        val barChart = binding.barChartMypage

        // barChart에 표시할 데이터 (우선 더미값으로 설정해둠. 추후 데이터 불러와서 y만 수정 필요)
        val entries = listOf(
            BarEntry(0.5f, 2f),  // Sunday
            BarEntry(1.5f, 4f),  // Monday
            BarEntry(2.5f, 6f),  // Tuesday
            BarEntry(3.5f, 3f),  // Wednesday
            BarEntry(4.5f, 5f),  // Thursday
            BarEntry(5.5f, 5f),  // Friday
            BarEntry(6.5f, 4f)   // Saturday
        )

        // barChart의 데이터
        val barDataSet = BarDataSet(entries, "Weekly Data").apply {
            color = ContextCompat.getColor(requireContext(), R.color.main_green) // 막대 색상
            setDrawValues(false) // 데이터 값 표시 비활성화
        }

        val barData = BarData(barDataSet).apply {
            barWidth = 0.8f // 막대 너비
        }

        barChart.apply {
            data = barData
            description.isEnabled = false
            legend.isEnabled = false
            setDrawGridBackground(false) // 차트 배경 그리드 채우기 비활성화

            // x축 설정
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                axisMinimum = 0f  // x축 최소값 설정
                axisMaximum = 7f  // x축 최대값 설정
                setDrawGridLines(true) // x축 그리드 라인 활성화
                enableGridDashedLine(15f, 10f, 0f) // 점선으로 설정
                setCenterAxisLabels(true)
                valueFormatter = object : ValueFormatter() {
                    private val days = arrayOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")
                    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                        return days.getOrNull(value.toInt()) ?: ""
                    }
                }

                // 라벨 폰트 설정
                typeface = resources.getFont(R.font.jua_400)
                textSize = 12f
            }

            // y축 값 표시 비활성화
            axisLeft.isEnabled = false
            axisRight.isEnabled = false

            notifyDataSetChanged()
            invalidate()
        }
    }

    private fun setupCheckBoxListeners() {
        binding.checkboxInProgress.setOnCheckedChangeListener { _, _ ->
            updateFilteredBoxes()
        }

        binding.checkboxCompleted.setOnCheckedChangeListener { _, _ ->
            updateFilteredBoxes()
        }
    }

    // 보여줄 시소 박스들만 필터링하는 함수
    private fun updateFilteredBoxes() {
        val inProgressChecked = binding.checkboxInProgress.isChecked
        val completedChecked = binding.checkboxCompleted.isChecked

        val filteredData = when {
            inProgressChecked && completedChecked -> ongoingData + completedData
            inProgressChecked -> ongoingData
            completedChecked -> completedData
            else -> emptyList()
        }

        updateSisoBoxes(filteredData)
    }

    private fun setupDropdownMenu() {
        binding.clMyPageSubTopBar.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), binding.clMyPageSubTopBar)
            popupMenu.menuInflater.inflate(R.menu.home_dropdown_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_recent -> {
                        binding.tvMyPageDown.text = "최근 생성순"
                        isRecentOrder = true
                        updateFilteredBoxes()
                    }

                    R.id.menu_balance -> {
                        binding.tvMyPageDown.text = "밸런스순"
                        isRecentOrder = false
                        updateFilteredBoxes()
                    }
                }
                true
            }

            popupMenu.show()
        }
    }

    private fun updateSisoBoxes(data: List<Seesaw>) {
        val sortedData = if (isRecentOrder) {
            data // '최근 생성순' => 그냥 리스트에 더미 데이터가 추가된 순서대로 정렬
        } else {
            data.sortedByDescending {
                it.toDoCount + it.notToDoCount - Math.abs(it.toDoCount - it.notToDoCount)
            }
        }

        val layouts = listOf(
            binding.clMyPageSeesaw1 to Pair(binding.tvMyPageSeesaw1, binding.ivMyPageSeesaw1),
            binding.clMyPageSeesaw2 to Pair(binding.tvMyPageSeesaw2, binding.ivMyPageSeesaw2),
            binding.clMyPageSeesaw3 to Pair(binding.tvMyPageSeesaw3, binding.ivMyPageSeesaw3),
            binding.clMyPageSeesaw4 to Pair(binding.tvMyPageSeesaw4, binding.ivMyPageSeesaw4),
            binding.clMyPageSeesaw5 to Pair(binding.tvMyPageSeesaw5, binding.ivMyPageSeesaw5),
            binding.clMyPageSeesaw6 to Pair(binding.tvMyPageSeesaw6, binding.ivMyPageSeesaw6),
            binding.clMyPageSeesaw7 to Pair(binding.tvMyPageSeesaw7, binding.ivMyPageSeesaw7),
            binding.clMyPageSeesaw8 to Pair(binding.tvMyPageSeesaw8, binding.ivMyPageSeesaw8)
        )

        // data 리스트 크기만큼 처리
        layouts.forEachIndexed { index, (layout, views) ->
            if (index < sortedData.size) {
                // 텍스트와 이미지를 데이터로 설정
                views.first.text = sortedData[index].title
                views.second.setImageResource(sortedData[index].imageResId)

                // 해당 레이아웃을 보이게 설정
                layout.visibility = View.VISIBLE
            } else {
                // 데이터가 없다면 해당 시소는 숨김 처리
                layout.visibility = View.GONE
            }
        }
    }
}