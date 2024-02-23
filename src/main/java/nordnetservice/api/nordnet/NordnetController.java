package nordnetservice.api.nordnet;

import nordnetservice.api.nordnet.dto.OpeningPriceDTO;
import nordnetservice.api.nordnet.response.CallsResponse;
import nordnetservice.api.nordnet.response.StockOptionDTO;
import nordnetservice.api.nordnet.response.StockPriceDTO;
import nordnetservice.api.response.DefaultResponse;
import nordnetservice.api.response.AppStatusCode;
import nordnetservice.domain.core.Core;
import nordnetservice.domain.stock.OpeningPrice;
import nordnetservice.domain.stock.StockPrice;
import nordnetservice.domain.stock.StockTicker;
import nordnetservice.domain.stockoption.StockOption;
import nordnetservice.dto.YearMonthDTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Function;


@Controller
@RequestMapping("/nordnet")
public class NordnetController {

    private final Core core;

    public NordnetController(Core core) {
        this.core = core;
    }

    @GetMapping(value = "/openingprice/{oid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OpeningPriceDTO> openingPrice(@PathVariable("oid") int oid) {
        var ticker = new StockTicker(oid);
        OpeningPrice price = core.openingPrice(ticker);
        return ResponseEntity.ok(new OpeningPriceDTO(price));
    }

    @GetMapping(value = "/spot/{oid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StockPriceDTO> spot(@PathVariable("oid") int oid) {
        var ticker = new StockTicker(oid);
        StockPrice price = core.getStockPrice(ticker);
        return ResponseEntity.ok(new StockPriceDTO(price));
    }

    @GetMapping(value = "/calls/{oid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CallsResponse> calls(@PathVariable("oid") int oid) {
        return ResponseEntity.ok(getOptions(oid, core::getCalls));
    }

    @GetMapping(value = "/puts/{oid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CallsResponse> puts(@PathVariable("oid") int oid) {
        return ResponseEntity.ok(getOptions(oid, core::getPuts));
    }

    @PostMapping(value = "/thirdfriday/nordnetmillis", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultResponse> thirdFridayNordnetMillis(@RequestBody List<YearMonthDTO> items) {
        core.thirdFridayMillis(items);
        return ResponseEntity.ok(new DefaultResponse(AppStatusCode.Ok, "ok"));
    }

    /*
    @PostMapping(value = "/thirdfriday/demo", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void thirdFridayDemo(@RequestBody List<YearMonthDTO> items, HttpServletResponse response) {
        System.out.println(response);
        //response.setStatus(HttpStatus.NO_CONTENT.value());
        System.out.println(items);
    }
     */

    private CallsResponse getOptions(int oid, Function<StockTicker,List<StockOption>> fn) {
        var ticker = new StockTicker(oid);
        var options = fn.apply(ticker);
        StockPrice price = core.getStockPrice(ticker);
        return new CallsResponse(new StockPriceDTO(price), map(options));
    }

    private List<StockOptionDTO> map(List<StockOption> options) {
        return options.stream().map(StockOptionDTO::new).toList();
    }
}

/*
@RestController
public class RepoController {
    @RequestMapping(value = "/document/{id}", method = RequestMethod.GET)
    public Object getDocument(@PathVariable long id, HttpServletResponse response) {
       Object object = getObject();
       if( null == object ){
          response.setStatus( HttpStatus.SC_NO_CONTENT);
       }
       return object ;
    }
}

@RestController
public class RepoController {
    @RequestMapping(value = "/document/{id}", method = RequestMethod.GET)
    public Object getDocument(@PathVariable long id) {
       Object object = getObject();
       if ( null == object ){
          return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
       }

       return object ;
    }
}

package your.package.filter;

import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class NoContentFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        filterChain.doFilter(httpServletRequest, httpServletResponse);
        if (httpServletResponse.getContentType() == null ||
                httpServletResponse.getContentType().equals("")) {
            httpServletResponse.setStatus(HttpStatus.NO_CONTENT.value());
        }
    }
}

@Aspect
public class NoContent204HandlerAspect {

  @Pointcut("execution(public * xx.xxxx.controllers.*.*(..))")
  private void anyControllerMethod() {
  }

  @Around("anyControllerMethod()")
  public Object handleException(ProceedingJoinPoint joinPoint) throws Throwable {

    Object[] args = joinPoint.getArgs();

    Optional<HttpServletResponse> response = Arrays.asList(args).stream().filter(x -> x instanceof HttpServletResponse).map(x -> (HttpServletResponse)x).findFirst();

    if (!response.isPresent())
      return joinPoint.proceed();

    Object retVal = joinPoint.proceed();
    if (retVal == null)
      response.get().setStatus(HttpStatus.NO_CONTENT.value());

    return retVal;
  }
}

 */
