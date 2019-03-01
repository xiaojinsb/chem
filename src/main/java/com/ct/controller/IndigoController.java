package com.ct.controller;

import com.ct.util.R;
import com.epam.indigo.Indigo;
import com.epam.indigo.IndigoObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/indigo")
public class IndigoController {

    Indigo indigo = new Indigo();


    //如果没有info方法 不会显示 高级功能按钮
    @GetMapping("/info")
    public R info() {
        return R.ok().put("indigo_version", indigo.version());
    }

    //整理
    @PostMapping("/layout")
    public R layout(@RequestBody Map<String, Object> reqMap) {
        IndigoObject mol = indigo.loadMolecule((String) reqMap.get("struct"));
        mol.layout();
        return R.ok().put("struct", mol.molfile());
    }

    //图形2d化
    @PostMapping("/clean")
    public R clean(@RequestBody Map<String, Object> reqMap) {
        IndigoObject mol = indigo.loadMolecule((String) reqMap.get("struct"));
        mol.clean2d();
        return R.ok().put("struct", mol.molfile());
    }

    //aromatize结构 圆
    @PostMapping("/aromatize")
    public R aromatize(@RequestBody Map<String, Object> reqMap) {
        IndigoObject mol = indigo.loadMolecule((String) reqMap.get("struct"));
        mol.aromatize();
        return R.ok().put("struct", mol.molfile());
    }

    //dearomatize结构  默认结构 三条线
    @PostMapping("/dearomatize")
    public R dearomatize(@RequestBody Map<String, Object> reqMap) {
        IndigoObject mol = indigo.loadMolecule((String) reqMap.get("struct"));
        mol.dearomatize();
        return R.ok().put("struct", mol.molfile());
    }

    //大概用于转码不会出错
    @PostMapping("/calculate_cip")
    public R calculate_cip(@RequestBody Map<String, Object> reqMap) {
        IndigoObject mol = indigo.loadMolecule((String) reqMap.get("struct"));
        mol.markEitherCisTrans();
        mol.markStereobonds();
        return R.ok().put("struct", mol.molfile());
    }

    //分析化学式的错误
    @PostMapping("/check")
    public Map<String, String> check(@RequestBody Map<String, Object> reqMap) {
        IndigoObject mol = indigo.loadMolecule((String) reqMap.get("struct"));
        Map<String, String> map = new HashMap<String, String>();
        map.put("valence",mol.checkBadValence());
        return map;
    }

    //化学式计算结果
    @PostMapping("/calculate")
    public R calculate(@RequestBody Map<String, Object> reqMap) {
        IndigoObject mol = indigo.loadMolecule((String) reqMap.get("struct"));
        return R.ok().put("molecular-weight", mol.molecularWeight()).put("most-abundant-mass", mol.mostAbundantMass()).put("monoisotopic-mass", mol.monoisotopicMass()).put("gross", mol.grossFormula()).put("mass-composition", mol.massComposition());
    }


    //转码 3d功能需要cml编码
    @PostMapping("/convert")
    public R convert(@RequestBody Map<String, Object> reqMap) {
        String format = (String) reqMap.get("output_format");
        IndigoObject mol = indigo.loadMolecule((String) reqMap.get("struct"));
        String output_format = "";
        System.out.println(format);
        if (format.equals("chemical/x-chemaxon-cxsmiles")) {
            output_format = "暂不支持";
        } else if (format.equals("chemical/x-daylight-smarts")) {
            output_format = "暂不支持";
        } else if (format.equals("chemical/x-inchi")) {
            output_format = "暂不支持";
        } else if (format.equals("chemical/x-inchi-aux")) {
            output_format = "暂不支持";
        } else if (format.equals("chemical/x-cml")) {
            output_format = mol.cml();
        }
        return R.ok().put("struct", output_format);
    }
}
