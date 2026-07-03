-- 查看现有科室
SELECT id, dept_name FROM department;

-- 查看现有医生
SELECT d.id, d.name, d.dept_id, dept.dept_name, d.title, d.remark 
FROM doctor d 
LEFT JOIN department dept ON d.dept_id = dept.id 
ORDER BY d.dept_id, d.id;
