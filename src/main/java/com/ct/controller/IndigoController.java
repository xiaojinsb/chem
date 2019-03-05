package com.ct.controller;

import com.ct.util.R;
import com.epam.indigo.Indigo;
import com.epam.indigo.IndigoObject;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
        map.put("valence", mol.checkBadValence());
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

    public String[] molArr() {
        File file = new File("D:/mol.txt");
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String mols = new String(filecontent);
        String[] molArr = mols.split("####################");
        return molArr;
    }

    //多线程
    class ThreadTest extends Thread {
        String[] molArr = molArr();
        private int begin;
        private int end;
        int yes = 0;
        int no = 0;

        @Override
        public synchronized void run() {
            for (int i = begin; i < end; i++) {
                molArr[i] = molArr[i].replace(molArr[i].split("  ")[0], "\n");
                try {
                    IndigoObject mol1 = indigo.loadMolecule("C1=CC=CC=C1");
                    IndigoObject mol2 = indigo.loadMolecule(molArr[i]);
                    indigo.similarity(mol1, mol2, "tversky");
                    yes += 1;
                } catch (Exception e) {
                    no += 1;
                }
            }
            System.out.println("正常计算：" + yes);
            System.out.println("错误计算：" + no);
        }

        public ThreadTest(int begin, int end) {
            this.begin = begin;
            this.end = end;
        }
    }

    @GetMapping("/search")
    public R search() throws InterruptedException {
        ThreadTest thread1 = new ThreadTest(0, 500);
        ThreadTest thread2 = new ThreadTest(500, 1000);
        ThreadTest thread3 = new ThreadTest(1000, 1491);
        thread1.start();
        thread2.start();
        thread3.start();
        return R.ok();
    }

    //单线程
    @GetMapping("/search1")
    public R search1() {
        String[] molArr = molArr();
        int yes = 0;
        int no = 0;
        for (int i = 0; i < 1491; i++) {
            molArr[i] = molArr[i].replace(molArr[i].split("  ")[0], "\n");
            try {
                IndigoObject mol1 = indigo.loadMolecule("C1=CC=CC=C1");
                IndigoObject mol2 = indigo.loadMolecule(molArr[i]);
                indigo.similarity(mol1, mol2, "euclid-sub");
                yes += 1;
            } catch (Exception e) {
                no += 1;
            }
        }

        System.out.println("正常计算：" + yes);
        System.out.println("错误计算：" + no);
        return R.ok();
    }
}
