package com.example.calculatortime

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
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


        //Инициализация UI элементов
        firstTimeET = findViewById(R.id.firstTimeET)
        secondTimeET = findViewById(R.id.secondTimeET)
        resultTV = findViewById(R.id.resultTV)
        operatorTV = findViewById(R.id.operatorTV)
        addOperationBTN = findViewById(R.id.addOperationBTN)
        subtrackOperationBTN = findViewById(R.id.subtrackOperationBTN)

        //Установка значений по умолчанию
        firstTimeET.setText("1m23s")
        secondTimeET.setText("3m48s")
        updateOperatorDisplay()

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
        //Вычисляем нанчальный результат
        calculateTime()
    }

    private fun updateOperatorDisplay() {
        operatorTV.text = if (isAddition) "+" else "-"

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
                var diff = timeOneSeconds - timeTwoSeconds
                if (diff < 0) {
                    //Для отрицательного результата покажем в формате "-1m30s"
                    diff
                } else {
                    diff
                }
            }

            //Конвертация результата обратно в формат времени
            val resultTime = formateSecondsToTime(resultSeconds)

            //Отображение результата
            resultTV.text = "$resultTime ($resultSeconds секунд)"
        } catch (e: Exception) {
            Toast.makeText(
                this, "Ошибка формата времени",
                Toast.LENGTH_SHORT
            ).show()
            e.printStackTrace()
        }
    }


    private fun formateSecondsToTime(totalSeconds: Int): String {
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return buildString {
            if (hours > 0) {
                append("${hours}ч")
            }
            if (minutes > 0) {
                append("${minutes}мин")
            }
            if (seconds > 0 || totalSeconds == 0) {
                append("${seconds}сек")
            }
        }
    }

    private fun parseTimeToSeconds(timeStr: String): Int {
        var totalSeconds = 0
        var remainingStr = timeStr.lowercase()

        //Регулярные выражения для поиска компонентов времени
        val hourPattern = Regex("""(\d+)\s*h""")
        val minutePattern = Regex("""(\d+)\s*m""")
        val secondPattern = Regex("""(\d+)\s*s""")

        //Извлечение часов
        hourPattern.find(remainingStr)?.let { match ->
            val hours = match.groupValues[1].toInt()
            totalSeconds += hours * 3600
            remainingStr = remainingStr.replace(match.value, "")
        }
        //Извлечение минут
        minutePattern.find(remainingStr)?.let { match ->
            val minutes = match.groupValues[1].toInt()
            totalSeconds += minutes * 60
            remainingStr = remainingStr.replace(match.value, "")
        }
        //Извлечение секунд
        secondPattern.find(remainingStr)?.let { match ->
            val seconds = match.groupValues[1].toInt()
            totalSeconds += seconds
            remainingStr = remainingStr.replace(match.value, "")
        }
        //Если остались только цифры (например "120"), считаем что это секунды
        val digitsOnly = Regex("""(\d+)""").find(remainingStr.trim())
        if (digitsOnly != null && totalSeconds == 0) {
            totalSeconds = digitsOnly.groupValues[1].toInt()
        }
        return totalSeconds
    }
}