package com.example.calculatortime

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var toolbarMain: androidx.appcompat.widget.Toolbar
    private lateinit var firstTimeET: EditText
    private lateinit var secondTimeET: EditText

    private lateinit var resultTV: TextView
    private lateinit var operatorTV: TextView

    private lateinit var addOperationBTN: Button
    private lateinit var subtrackOperationBTN: Button

    //Переменная дл отслеживания текущей операции
    private var isAddition = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbarMain = findViewById(R.id.toolbarMain)
        setSupportActionBar(toolbarMain)
        title = "Калькулятор времени"
        toolbarMain.subtitle = "версия 1.0"
        toolbarMain.setLogo(R.drawable.ic_calculate)
        initView()
        setupListeners()
        calculateTime()
        testFormatSecondsToTime()
    }

    private fun initView() {
        //Инициализация UI элементов
        firstTimeET = findViewById(R.id.firstTimeET)
        secondTimeET = findViewById(R.id.secondTimeET)
        resultTV = findViewById(R.id.resultTV)
        operatorTV = findViewById(R.id.operatorTV)
        addOperationBTN = findViewById(R.id.addOperationBTN)
        subtrackOperationBTN = findViewById(R.id.subtrackOperationBTN)

        //Установка значений по умолчанию
        firstTimeET.setText("1h65m23s")
        secondTimeET.setText("5h2548s")
        updateOperatorDisplay()
    }

    private fun setupListeners() {
        //Обработчики кнопок
        addOperationBTN.setOnClickListener {
            isAddition = true
            updateOperatorDisplay()
            calculateTime()
        }
        subtrackOperationBTN.setOnClickListener {
            isAddition = false
            updateOperatorDisplay()
            calculateTime()
        }

        //Слушатели изменения текста для автоматического пересчета
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                calculateTime()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        firstTimeET.addTextChangedListener(textWatcher)
        secondTimeET.addTextChangedListener(textWatcher)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.resetMenuMain -> {
                firstTimeET.text.clear()
                secondTimeET.text.clear()
                resultTV.text = "Результат"
                Toast.makeText(
                    applicationContext,
                    "Данные очищены",
                    Toast.LENGTH_LONG
                ).show()
            }

            R.id.exitMenuMain -> {
                Toast.makeText(
                    applicationContext,
                    "Работа завершена",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateOperatorDisplay() {
        operatorTV.text = if (isAddition) "+" else "-"

        //Обновляем цвета кнопок
        val activeColorAdd = if (isAddition) R.color.green_active else R.color.green_inactive
        val activeColorSubtract = if (isAddition) R.color.red_active else R.color.red_inactive
    }

    private fun calculateTime() {
        try {
            val timeOneStr = firstTimeET.text.toString().trim()
            val timeTwoStr = secondTimeET.text.toString().trim()

            if (timeOneStr.isEmpty() || timeTwoStr.isEmpty()) {
                Toast.makeText(
                    this, "Введите оба значения времени",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            //Конвертация в секунды
            val timeOneSeconds = parseTimeToSeconds(timeOneStr)
            val timeTwoSeconds = parseTimeToSeconds(timeTwoStr)

            //Выполнение операции
            val resultSeconds = if (isAddition) {
                timeOneSeconds + timeTwoSeconds
            } else {
                timeOneSeconds - timeTwoSeconds
            }

            //Конвертация результата обратно в формат времени
            val resultTime = formateSecondsToTime(resultSeconds)
            resultTV.text = resultTime

//            //Отображение результата
//            resultTV.text = "$resultTime ($resultSeconds секунд)"
        } catch (e: Exception) {
            resultTV.text = "Ошибка"
            Toast.makeText(
                this, "Некорректный формат времени",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun formateSecondsToTime(totalSeconds: Int): String {
        if (totalSeconds == 0) return "0s"
        val isNegative = totalSeconds < 0
        var remainingSeconds = Math.abs(totalSeconds)
        //Вычисляем часы минуты секунды
        val hours = remainingSeconds / 3600
        remainingSeconds %= 3600
        val minutes = remainingSeconds / 60
        val seconds = remainingSeconds % 60

        //Форматируем результат
        return buildString {

            if (isNegative) append("-")

            when {
                hours > 0 -> append("${hours}h${minutes}m${seconds}s")
                minutes > 0 -> append("${minutes}m${seconds}s")
                else -> append("${seconds}s")
            }
        }
    }
    private fun parseTimeToSeconds(timeStr: String): Int {
        var totalSeconds = 0
        var remainingStr = timeStr.lowercase()
        //паттерны для часов, минут, секунд
        val patterns = listOf(
            Regex("""(\d+)\s*h""") to 3600, // часы
            Regex("""(\d+)\s*m""") to 60, //минуты
            Regex("""(\d+)\s*s""") to 1   //сеекунды
        )

        for ((pattern, multiplier) in patterns) {
            pattern.find(remainingStr)?.let { match ->
                val value = match.groupValues[1].toInt()
                totalSeconds += value * multiplier
                remainingStr = remainingStr.replace(match.value, "")
            }
        }

        //Если остались только цифры (например "120"), считаем что это секунды
        val digitsOnly = Regex("""(\d+)""").find(remainingStr.trim())
        if (digitsOnly != null && totalSeconds == 0) {
            totalSeconds = digitsOnly.groupValues[1].toInt()
        }
        return totalSeconds
    }

    private fun testFormatSecondsToTime() {
        val testCases = listOf(
            0 to "0s",
            45 to "45s",
            60 to "1m0s",
            65 to "1m5s",
            3600 to "1h0m0s",
            3665 to "1h1m5s",
            -45 to "-45s",
            -60 to "-1m0s",
            -65 to "-1m5s",
            -3600 to "-1h0m0s",
            -3665 to "-1h1m5s",
            5 * 60 + 11 to "5m11s",
            (1 * 3600 + 23 * 60 + 36) to "1h23m36s"
        )

        for ((seconds, expected) in testCases) {
            val result = formateSecondsToTime(seconds)
            println("$seconds -> $result (ожидается: $expected) ${if(result == expected) "✔" else "✖"}")
        }
    }
}



