package com.example.tonottodo

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tonottodo.databinding.FragmentGoalSettingBinding


class GoalSettingFragment : Fragment() {
    private lateinit var binding: FragmentGoalSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentGoalSettingBinding.inflate(layoutInflater)

        // tv_goal_detail_title의 텍스트 설정
        setCustomText()

        binding.clGoalSetting.setOnClickListener{
            //박스 테두리 추가
            //binding.clGoalSetting.setBackgroundResource(R.drawable.bg_green_9)
            //editText 클릭 가능 & 입력 가능하게 변경
            binding.etGoalSetting.isClickable = true
            binding.etGoalSetting.isEnabled = true
            binding.etGoalSetting.requestFocus()
        }

        binding.btnGoalSettingNext.setOnClickListener {
            findNavController().navigate(R.id.action_goal_setting_to_add_todo)
        }

        //입력한 문쟈갸 없으면 버튼이 활성화되지 않도록 설정
        setupEditText()
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.etGoalSetting.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) initTodoEditTextSelection(binding.clGoalSetting)
        }

    }
    private fun initTodoEditTextSelection(view: View) {
        (binding.clGoalSetting).forEach { it.isSelected = false }
        view.isSelected = true
    }

    private fun setupEditText() {
        with(binding) {
            etGoalSetting.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
                override fun afterTextChanged(editable: Editable?) {
                    btnGoalSettingNext.isClickable = editable?.length ?: 0 > 0
                }
            })
        }
    }
    private fun setCustomText() {
        val text = "해야 할 일과 하지 말아야 할 일,\n일상 속 균형을 투낫투두가 잡아줄게!"
        val spannableString = SpannableString(text)

        // "투낫투두"의 색상을 변경 (예: 빨간색)
        val startIndex = text.indexOf("투낫투두")
        if (startIndex >= 0) {
            val endIndex = startIndex + "투낫투두".length
            val mainGreenColor = ContextCompat.getColor(requireContext(), R.color.main_green)

            spannableString.setSpan(
                ForegroundColorSpan(mainGreenColor),
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // 변경된 텍스트를 TextView에 적용
        binding.tvGoalDetailTitle.text = spannableString
    }
}