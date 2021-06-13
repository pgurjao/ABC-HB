package br.edu.infnet.infra;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class ConverterListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static final String DATE_PATTERN = "yyyy-MM-dd";

    private static final String MONTH_PATTERN = "yyyy-MM";

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() == null) {//父容器为null就表示是root applicationContext

            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            ConvertUtils.register(new Converter() {
                @Override
                public <T> T convert(Class<T> type, Object value) {

                    try {
                        Date date = DateUtils.parseDate((String) value, new String[]{DATETIME_PATTERN, DATE_PATTERN, MONTH_PATTERN});
                        return (T) date;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    return null;
                }
            }, Date.class);
        }
    }
}
