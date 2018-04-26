package com.algaworks.algamoney.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
/**
 * Essa classe apenas habilita as tarefas cron do spring
 * @author paulo
 *
 */
@Configuration // informa o spring q eh configuracao
@EnableScheduling // habilita as tarefas anotadas com @Scheduled (LancamentoService) 
public class CronScheduleConfig {

}
