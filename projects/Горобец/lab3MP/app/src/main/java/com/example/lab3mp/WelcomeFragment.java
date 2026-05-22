package com.example.lab3mp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class WelcomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_welcome, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button loadButton = view.findViewById(R.id.loadButton);
        Button infoButton = view.findViewById(R.id.infoButton);

        LinearLayout popup = view.findViewById(R.id.popupWindow);
        Button closePopup = view.findViewById(R.id.closePopup);
        TextView popupText = view.findViewById(R.id.popupText);

        // Переход к списку
        loadButton.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, new ListFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Открыть окно с текстом
        infoButton.setOnClickListener(v -> {
            popupText.setText(
                    "отображать список элементов внутри фрагмента\n" +
                            "список занимает более одного экрана (прокрутка)\n" +
                            "список можно пролистать\n" +
                            "отдельный элемент списка с пользовательским стилем/дизайном\n" +
                            "выполнять запрос на получение данных с удаленного сервера\n" +
                            "выполнять преобразование json-структуры в коллекцию объектов\n" +
                            "выделение отдельного элемента списка с отображение детальной информации\n" +
                            "отображать детальную информацию об элементе внутри отдельного фрагмента\n\n" +
                            "Бонусы:\n" +
                            "- Настройки приложения\n" +
                            "- Управление страницами\n" +
                            "- Несколько запросов\n" +
                            "- Сохранение данных\n" +
                            "- Передача данных\n" +
                            "- Собственный адаптер\n" +
                            "- Изображения\n" +
                            "- Обработка исключений"
            );

            popup.setVisibility(View.VISIBLE);
        });

        // Закрыть окно
        closePopup.setOnClickListener(v -> popup.setVisibility(View.GONE));
    }
}
