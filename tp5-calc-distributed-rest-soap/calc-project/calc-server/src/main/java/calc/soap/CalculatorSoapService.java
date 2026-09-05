package calc.soap;

import calc.model.HistoryRecord;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import java.util.List;

@WebService
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT)
public interface CalculatorSoapService {

    @WebMethod
    double add(@WebParam(name = "a") double a, @WebParam(name = "b") double b);

    @WebMethod
    double subtract(@WebParam(name = "a") double a, @WebParam(name = "b") double b);

    @WebMethod
    double multiply(@WebParam(name = "a") double a, @WebParam(name = "b") double b);

    @WebMethod
    double divide(@WebParam(name = "a") double a, @WebParam(name = "b") double b) throws Exception;

    @WebMethod
    List<HistoryRecord> getHistory();
}
