package com.stocktide.stocktideserver.stock.service;

import com.stocktide.stocktideserver.stock.dto.StockMinDto;
import com.stocktide.stocktideserver.stock.dto.StockasbiDataDto;
import com.stocktide.stocktideserver.stock.repository.CompanyRepository;
import com.stocktide.stocktideserver.util.Time;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;


@Service
@Transactional
@Slf4j
public class ApiCallService {
    @Getter
    @Value("${token.app-key}")
    private String APP_KEY;

    @Getter
    @Value("${token.app-secret}")
    private String APP_SECRET;

    @Getter
    @Value("${stock-url.token}")
    private String TOKEN_URL;

    @Getter
    @Value("${stock-url.stockasbi}")
    private String STOCKASBI_URL;

    @Getter
    @Value("${stock-url.stockhour}")
    private String STOCKHOUR_URL;

    @Getter
    @Value("${stock-url.kospi}")
    private String KOSPI_URL;


    private final String FID_ETC_CLS_CODE = "";
    private final String FID_COND_MRKT_DIV_CODE = "J";
    // private final String FID_INPUT_HOUR_1 = "153000";
    private final String FID_PW_DATA_INCU_YN = "Y";

    private RestTemplate restTemplate = new RestTemplate();

    public ApiCallService(TokenService tokenService, CompanyRepository companyRepository) {
        this.tokenService = tokenService;
        this.companyRepository = companyRepository;
    }

    private final TokenService tokenService;

    private final CompanyRepository companyRepository;

    public StockasbiDataDto getStockasbiDataFromApi(String stockCode){
        log.info("---------------getStockasbiDataFromApi  started----------------------------------------");
        String token = tokenService.getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        headers.add("appkey", APP_KEY);
        headers.add("appsecret", APP_SECRET);
        headers.add("tr_id", "FHKST01010200");

        //FID_COND_MRKT_DIV_CODE : 시장 분류 코드 (J : 주식)
        //FID_INPUT_ISCD : 종목번호

        String uri = STOCKASBI_URL + "?FID_COND_MRKT_DIV_CODE=J&FID_INPUT_ISCD=" + stockCode;

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        log.info("---------------getStockasbiDataFromApi  request send----------------------------------------");

        ResponseEntity<StockasbiDataDto> response = restTemplate.exchange(uri, HttpMethod.GET, entity, new ParameterizedTypeReference<StockasbiDataDto>() {});

        if (response.getStatusCode().is2xxSuccessful()) {
            StockasbiDataDto stockasbiDataDto = response.getBody();
            log.info("---------------getStockasbiDataFromApi successfully finished getOutput1 getAskp1: {}----------------------------------------", stockasbiDataDto.getOutput1().getAskp1());
            return stockasbiDataDto;
        } else {
            log.info("error");
            log.info("---------------getStockasbiDataFromApi  error----------------------------------------");
            return null;
        }

    }

    public StockMinDto getStockMinDataFromApi(String stockCode, String strHour) {
        log.info("---------------getStockMinDataFromApi  started----------------------------------------");
        String token = tokenService.getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        headers.add("appkey", APP_KEY);
        headers.add("appsecret", APP_SECRET);
        headers.add("tr_id", "FHKST03010200");

        //FID_COND_MRKT_DIV_CODE : 시장 분류 코드 (J : 주식)
        //FID_INPUT_ISCD : 종목번호 
        //FID_ETC_CLS_CODE : 기타 구분 코드("")
        //FID_INPUT_HOUR_1 : "123000" 입력 시 12시 30분 이전부터 1분 간격으로 조회
        //FID_PW_DATA_INCU_YN : N : 당일데이터만 조회  Y : 이후데이터도 조회

        String uri = STOCKHOUR_URL + "?FID_COND_MRKT_DIV_CODE=" + FID_COND_MRKT_DIV_CODE + "&FID_INPUT_ISCD=" + stockCode +  "&FID_ETC_CLS_CODE=" + FID_ETC_CLS_CODE
                + "&FID_INPUT_HOUR_1=" + strHour + "&FID_PW_DATA_INCU_YN=" +  FID_PW_DATA_INCU_YN;

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        ResponseEntity<StockMinDto> response = restTemplate.exchange(uri, HttpMethod.GET, entity, new ParameterizedTypeReference<StockMinDto>() {});

        if (response.getStatusCode().is2xxSuccessful()) {
            StockMinDto stockMinDto = response.getBody();
            log.info("---------------getStockMinDataFromApi  finished----------------------------------------");

            return stockMinDto;

        } else {
            log.info("error");
            log.info("---------------getStockMinDataFromApi  err----------------------------------------");

            return null;
        }

    }

    public String getKospiMonthFromApi(){
        String token = tokenService.getAccessToken();

        LocalDateTime localDateTime = LocalDateTime.now();

        String strMonth = Time.strMonth(localDateTime);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        headers.add("appkey", APP_KEY);
        headers.add("appsecret", APP_SECRET);
        headers.add("tr_id", "FHKUP03500100");

        //FID_COND_MRKT_DIV_CODE : 시장 분류 코드 (U)
        //FID_INPUT_ISCD : (0001 : 종합 0002 : 대형주 )
        //FID_INPUT_DATE_1 :조회 시작일자 (ex. 20220501)
        //FID_INPUT_DATE_2 : 조회 종료일자 (ex. 20220530)
        //FID_PERIOD_DIV_CODE : D:일봉, W:주봉, M:월봉, Y:년봉

        String uri = KOSPI_URL + "?FID_COND_MRKT_DIV_CODE=U&FID_INPUT_ISCD=" + "0001" + "&FID_INPUT_DATE_1=" + "20230101"
                +"&FID_INPUT_DATE_2=" + strMonth + "&FID_PERIOD_DIV_CODE=" + "M";

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            log.info("error");
            return null;
        }

    }

}
