package manas.muna.bestautotrade.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//@Configuration
@Component("initializeStockName")
public class InitializeStockName {
    @Value("${a_stock_list}")
    String[] a_stock_list;
    @Value("${b_stock_list}")
    String[] b_stock_list;
    @Value("${c_stock_list}")
    String[] c_stock_list;
    @Value("${d_stock_list}")
    String[] d_stock_list;
    @Value("${e_stock_list}")
    String[] e_stock_list;
    @Value("${f_stock_list}")
    String[] f_stock_list;
    @Value("${g_stock_list}")
    String[] g_stock_list;
    @Value("${h_stock_list}")
    String[] h_stock_list;
    @Value("${i_stock_list}")
    String[] i_stock_list;
    @Value("${j_stock_list}")
    String[] j_stock_list;
    @Value("${k_stock_list}")
    String[] k_stock_list;
    @Value("${l_stock_list}")
    String[] l_stock_list;
    @Value("${m_stock_list}")
    String[] m_stock_list;
    @Value("${n_stock_list}")
    String[] n_stock_list;
    @Value("${o_stock_list}")
    String[] o_stock_list;
    @Value("${p_stock_list}")
    String[] p_stock_list;
    @Value("${q_stock_list}")
    String[] q_stock_list;
    @Value("${r_stock_list}")
    String[] r_stock_list;
    @Value("${s_stock_list}")
    String[] s_stock_list;
    @Value("${t_stock_list}")
    String[] t_stock_list;
    @Value("${u_stock_list}")
    String[] u_stock_list;
    @Value("${v_stock_list}")
    String[] v_stock_list;
    @Value("${w_stock_list}")
    String[] w_stock_list;
    @Value("${x_stock_list}")
    String[] x_stock_list;
    @Value("${y_stock_list}")
    String[] y_stock_list;
    @Value("${z_stock_list}")
    String[] z_stock_list;
    @Value("${index_list}")
    String[] index_stock_list;

    public List<String> getAStocks() {
        return Stream.of(a_stock_list).collect(Collectors.toList());
    }

    public List<String> getBStocks() {
        return Stream.of(b_stock_list).collect(Collectors.toList());
    }
    public List<String> getCStocks() {
        return Stream.of(c_stock_list).collect(Collectors.toList());
    }
    public List<String> getDStocks() {
        return Stream.of(d_stock_list).collect(Collectors.toList());
    }
    public List<String> getEStocks() {
        return Stream.of(e_stock_list).collect(Collectors.toList());
    }
    public List<String> getFStocks() {
        return Stream.of(f_stock_list).collect(Collectors.toList());
    }
    public List<String> getGStocks() {
        return Stream.of(g_stock_list).collect(Collectors.toList());
    }
    public List<String> getHStocks() {
        return Stream.of(h_stock_list).collect(Collectors.toList());
    }
    public List<String> getIStocks() {
        return Stream.of(i_stock_list).collect(Collectors.toList());
    }
    public List<String> getJStocks() {
        return Stream.of(j_stock_list).collect(Collectors.toList());
    }
    public List<String> getKStocks() {
        return Stream.of(k_stock_list).collect(Collectors.toList());
    }
    public List<String> getLStocks() {
        return Stream.of(l_stock_list).collect(Collectors.toList());
    }
    public List<String> getMStocks() {
        return Stream.of(m_stock_list).collect(Collectors.toList());
    }
    public List<String> getNStocks() {
        return Stream.of(n_stock_list).collect(Collectors.toList());
    }
    public List<String> getOStocks() {
        return Stream.of(o_stock_list).collect(Collectors.toList());
    }
    public List<String> getPStocks() {
        return Stream.of(p_stock_list).collect(Collectors.toList());
    }
    public List<String> getQStocks() {
        return Stream.of(q_stock_list).collect(Collectors.toList());
    }
    public List<String> getRStocks() {
        return Stream.of(r_stock_list).collect(Collectors.toList());
    }
    public List<String> getSStocks() {
        return Stream.of(s_stock_list).collect(Collectors.toList());
    }
    public List<String> getTStocks() {
        return Stream.of(t_stock_list).collect(Collectors.toList());
    }
    public List<String> getUStocks() {
        return Stream.of(u_stock_list).collect(Collectors.toList());
    }
    public List<String> getVStocks() {
        return Stream.of(v_stock_list).collect(Collectors.toList());
    }
    public List<String> getWStocks() {
        return Stream.of(w_stock_list).collect(Collectors.toList());
    }
    public List<String> getXStocks() {
        return Stream.of(x_stock_list).collect(Collectors.toList());
    }
    public List<String> getYStocks() {
        return Stream.of(y_stock_list).collect(Collectors.toList());
    }
    public List<String> getZStocks() {
        return Stream.of(z_stock_list).collect(Collectors.toList());
    }
    public List<String> getIndexStocks() {
        return Stream.of(index_stock_list).collect(Collectors.toList());
    }

    public List<String> getAllAZStockNames() {
        return Stream.of(a_stock_list,b_stock_list,c_stock_list,d_stock_list,e_stock_list,f_stock_list,g_stock_list,
                h_stock_list,i_stock_list,j_stock_list,k_stock_list,l_stock_list,m_stock_list,n_stock_list,o_stock_list,
                p_stock_list,q_stock_list,r_stock_list,s_stock_list,t_stock_list,u_stock_list,v_stock_list,w_stock_list,
                x_stock_list,y_stock_list,z_stock_list).flatMap(x-> Arrays.stream(x)).collect(Collectors.toList());
    }
}
